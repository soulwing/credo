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
import java.util.List;

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
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;
import org.soulwing.credo.service.protect.CredentialProtectionService;

/**
 * A concrete implementation of {@link ImportService}.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteImportService implements ImportService {

  @Inject
  protected CredentialImporterFactory importerFactory;
  
  @Inject
  protected CredentialRepository credentialRepository;
  
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
  protected CredentialProtectionService protectionService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ImportDetails prepareImport(List<FileContentModel> files,
      Errors errors, Password passphrase) 
      throws PassphraseException, ImportException {
    CredentialImporter importer = importerFactory.newImporter();
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
      ProtectionParameters protection, Errors errors) throws NoSuchGroupException, PassphraseException,
      AccessDeniedException {

    try {
      Credential credential = createCredential(details);    
      credential.setOwner(resolveOwner(protection, errors));
      protectionService.protect(credential, details.getPrivateKey(), 
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
      throw new AccessDeniedException();
    }
    catch (NoSuchGroupException ex) {
      errors.addError("owner", "credentialOwnerNotFound", 
          protection.getGroupName());
      throw ex;
    }
  }
  
  private UserGroup resolveOwner(ProtectionParameters protection, 
      Errors errors) throws NoSuchGroupException {
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
      catch (GroupEditException gex) {
        throw new RuntimeException(gex);
      }
      catch (PassphraseException pex) {
        throw new RuntimeException(pex);
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
  public void saveCredential(Credential credential, Errors errors)
      throws ImportException {
    credentialRepository.add(credential);
  }

}

