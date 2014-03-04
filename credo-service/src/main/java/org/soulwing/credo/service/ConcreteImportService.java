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
import java.security.PrivateKey;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.TagRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;

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
  protected TagRepository tagRepository;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserGroupMemberRepository groupMemberRepository;
  
  @Inject
  protected SecretKeyDecoder secretKeyDecoder;
  
  @Inject 
  @Encoded(type = Encoded.Type.PKCS8)
  protected PrivateKeyDecoder privateKeyDecoder;
  
  @Inject 
  protected PrivateKeyEncryptionService privateKeyEncryptionService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ImportPreparation prepareImport(List<FileContentModel> files,
      Errors errors) throws ImportException {
    CredentialImporter importer = importerFactory.newImporter();
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
    if (errors.hasErrors()) {
      throw new ImportException();
    }
    
    return importer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential createCredential(ImportPreparation preparation,
      Errors errors) throws ImportException, PassphraseException {
    if (!(preparation instanceof CredentialImporter)) {
      throw new IllegalArgumentException(
          "preparation was not created by this service");
    }

    CredentialImporter importer = (CredentialImporter) preparation;
    importer.validate(errors);
    return importer.build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void protectCredential(Credential credential,
      ImportPreparation preparation, ProtectionParameters protection,
      Errors errors) throws NoSuchGroupException, PassphraseException {
    
    UserGroupMember groupMember = groupMemberRepository
        .findByGroupAndLoginName(protection.getGroupName(), 
            protection.getLoginName());
    if (groupMember == null) {
      errors.addError("owner", "credentialOwnerNotFound");
      throw new NoSuchGroupException();
    }
    
    try {
      PrivateKeyWrapper encryptedPrivateKey = privateKeyDecoder.decode(
          groupMember.getUser().getPrivateKey());
      encryptedPrivateKey.setProtectionParameter(protection.getPassword());
      PrivateKey userPrivateKey = encryptedPrivateKey.derive();
      
      SecretKeyWrapper encryptedSecretKey = secretKeyDecoder.decode(
          groupMember.getSecretKey()); 
  
      encryptedSecretKey.setPrivateKey(userPrivateKey);
      PrivateKeyWrapper credentialPrivateKey = 
          privateKeyEncryptionService.encrypt(
              preparation.getDetails().getPrivateKey(), 
              encryptedSecretKey.derive());
      
      credential.setOwner(groupMember.getGroup());
      credential.getPrivateKey().setContent(credentialPrivateKey.getContent());
    }
    catch (IncorrectPassphraseException ex) {
      errors.addError("password", "passwordIncorrect");
      throw new PassphraseException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void saveCredential(Credential credential, Errors errors)
      throws ImportException {
    credentialRepository.add(credential);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<? extends Tag> resolveTags(String[] tokens) {
    Set<Tag> tags = new LinkedHashSet<>();
    for (String token : tokens) {
      Tag tag = tagRepository.findByTagText(token);
      if (tag == null) {
        tag = tagRepository.newTag(token);
      }
      tags.add(tag);
    }
    return tags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<? extends UserGroup> getGroupMemberships(String loginName) {
    return groupRepository.findByLoginName(loginName);
  }
  
}

