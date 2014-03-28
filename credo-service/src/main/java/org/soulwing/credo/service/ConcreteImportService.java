/*
 * File created on Feb 14, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.credo.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialCertificateBuilder;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.protect.CredentialRequestProtectionService;

/**
 * A concrete implementation of {@link ImportService}.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteImportService implements ImportService {

  private static final Password EMPTY_PASSPHRASE = new Password(new char[0]);

  @Inject
  protected CredentialImporterFactory importerFactory;
  
  @Inject
  protected CredentialRepository credentialRepository;
  
  @Inject
  protected CredentialRequestRepository requestRepository;
  
  @Inject
  protected CredentialBuilderFactory credentialBuilderFactory;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @Inject
  protected TagService tagService;
  
  @Inject
  protected GroupService groupService;
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected CredentialRequestProtectionService requestProtectionService;

  @Inject
  protected CredentialProtectionService credentialProtectionService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequest findRequestById(Long id)
      throws NoSuchCredentialException {
    CredentialRequest request = requestRepository.findById(id);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    return request;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImportDetails prepareImport(List<FileContentModel> files,
      Password passphrase, Errors errors) 
      throws PassphraseException, ImportException {
    CredentialImporter importer = importerFactory.newImporter();
    return prepareImport(importer, files, passphrase, errors);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImportDetails prepareImport(CredentialRequest request,
      List<FileContentModel> files, ProtectionParameters protection,
      Errors errors) throws PassphraseException, GroupAccessException,
      ImportException {
    try {
      PrivateKeyWrapper privateKey = requestProtectionService.unprotect(
          request, protection);
      CredentialImporter importer = importerFactory.newImporter(privateKey);
      ImportDetails details = prepareImport(importer, files, EMPTY_PASSPHRASE, 
          errors);
      details.setName(request.getName());
      details.setOwner(request.getOwner().getName());
      details.setNote(request.getNote());
      details.setTags(getTagNames(request.getTags()));
      return details;
    }
    catch (GroupAccessException ex) {
      errors.addError("groupAccessDenied", 
          new Object[] { protection.getGroupName() });
      throw ex;
    }
    catch (UserAccessException ex) {
      errors.addError("passwordIncorrect");
      throw new PassphraseException();
    }
  }

  private String[] getTagNames(Set<? extends Tag> tags) {
    String[] names = new String[tags.size()];
    int index = 0;
    Iterator<? extends Tag> i = tags.iterator();
    while (i.hasNext()) {
      names[index++] = i.next().getText(); 
    }
    return names;
  }
  
  private ImportDetails prepareImport(CredentialImporter importer,
      List<FileContentModel> files, Password passphrase, Errors errors)
      throws ImportException, PassphraseException {
    importFiles(importer, files, errors);
    if (errors.hasErrors()) {
      throw new ImportException();
    }
    return importer.validateAndImport(passphrase, errors);
  }

  private void importFiles(CredentialImporter importer,
      List<FileContentModel> files, Errors errors) throws ImportException {
    if (files.isEmpty()) {
      errors.addError("importFileRequired");
      throw new ImportException();
    }
    int i = 0;
    for (FileContentModel file : files) {
      try {
        importer.loadFile(file.getInputStream());        
        i++;
      }
      catch (NoContentException ex) {
        errors.addError("file" + i, "importNoContent",
            file.getName());
      }
      catch (IOException ex) {
        errors.addError("file" + i, "importReadError",
            file.getName());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential createCredential(ImportDetails details,
      ProtectionParameters protection, Errors errors) 
      throws NoSuchGroupException, GroupAccessException, PassphraseException {

    try {
      Credential credential = createCredential(details);    
      credential.setOwner(resolveOwner(protection, errors));
      credentialProtectionService.protect(credential, details.getPrivateKey(), 
          protection);
      return credential;
    }
    catch (UserAccessException ex) {
      errors.addError("password", "passwordIncorrect");
      throw new PassphraseException();
    }
    catch (GroupAccessException ex) {
      errors.addError("owner", "groupAccessDenied", 
          protection.getGroupName());
      throw ex;
    }
    catch (NoSuchGroupException ex) {
      errors.addError("owner", "credentialOwnerNotFound", 
          protection.getGroupName());
      throw ex;
    }
  }
  
  private UserGroup resolveOwner(ProtectionParameters protection, 
      Errors errors) throws NoSuchGroupException, GroupAccessException {
    UserGroup group = null;
    try {
      group = findOwnerGroup(protection);
    }
    catch (NoSuchGroupException ex) {
      GroupEditor editor = groupService.newGroup();
      editor.setName(protection.getGroupName());
      try {
        groupService.saveGroup(editor, errors);
        group = findOwnerGroup(protection);
      }
      catch (GroupEditException|PassphraseException|MergeConflictException oex) {
        throw new RuntimeException(oex);
      }
    }
    return group;
  }

  private UserGroup findOwnerGroup(ProtectionParameters protection)
      throws NoSuchGroupException {
    UserGroup group = groupRepository.findByGroupName(
        protection.getGroupName(), userContextService.getLoginName());
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return group;
  }
  
  private Credential createCredential(ImportDetails details) {
    CredentialBuilder builder = credentialBuilderFactory.newCredentialBuilder()
        .setName(details.getName())
        .setIssuer(details.getIssuerCommonName())
        .setNote(details.getNote())
        .setTags(tagService.resolve(details.getTags()))
        .setExpiration(details.getNotAfter())
        .setPrivateKey(details.getPrivateKey().getContent());
    
    for (CertificateWrapper certificate : details.getCertificates()) {
      builder.addCertificate(createCertificate(certificate));
    }
    return builder.build();
  }
  
  private CredentialCertificate createCertificate(
      CertificateWrapper certificate) {
    try {
      CredentialCertificateBuilder builder =
          credentialBuilderFactory.newCertificateBuilder();
      builder.setSubject(certificate.getSubject());
      builder.setIssuer(certificate.getIssuer());
      builder.setSerialNumber(certificate.getSerialNumber());
      builder.setNotBefore(certificate.getNotBefore());
      builder.setNotAfter(certificate.getNotAfter());
      builder.setContent(certificate.getContent());
      return builder.build();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveCredential(Credential credential, boolean removeRequest,
      Errors errors) throws ImportException, GroupAccessException {
    
    try {
      CredentialRequest request = credential.getRequest();
      boolean willRemoveRequest = request != null && removeRequest;
      if (willRemoveRequest) {
        credential.setRequest(null);
      }
      
      credentialRepository.add(credential);
      if (willRemoveRequest) {
        requestRepository.remove(requestRepository.update(request), false);
      }
    }
    catch (OwnerAccessControlException ex) {
      errors.addError("groupAccessDenied", new Object[] { ex.getGroupName() });
      throw new GroupAccessException(ex.getGroupName());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeCredential(Credential credential, Errors errors)
      throws GroupAccessException {
    try {
      credential.setRequest(null);
      credentialRepository.remove(credentialRepository.update(credential));
    }
    catch (OwnerAccessControlException ex) {
      errors.addError("groupAccessDenied", new Object[] { ex.getGroupName() });
      throw new GroupAccessException(ex.getGroupName());
    }
  }

}

