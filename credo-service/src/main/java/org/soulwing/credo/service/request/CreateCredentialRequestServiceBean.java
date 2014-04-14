/*
 * File created on Apr 12, 2014 
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
package org.soulwing.credo.service.request;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.TagService;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;

/**
 * A {@link CreateCredentialRequestService} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CreateCredentialRequestServiceBean implements CreateCredentialRequestService {

  static final String CONTENT_TYPE = "application/pkcs10";
  
  static final String CHARACTER_ENCODING = "US-ASCII";
  
  static final String SUFFIX = ".csr";
  
  @Inject
  protected CredentialRepository credentialRepository;

  @Inject
  protected CredentialRequestEditorFactory editorFactory;
  
  @Inject
  protected CredentialRequestGenerator generator;
  
  @Inject
  protected CredentialRequestRepository requestRepository;

  @Inject
  protected TagService tagService;

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestEditor createEditor() {
    return editorFactory.newEditor();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequestEditor createEditor(Long credentialId, Errors errors)
      throws NoSuchCredentialException {
    
    Credential credential = credentialRepository.findById(credentialId);
    if (credential == null) {
      errors.addError("credentialId", "credentialNotFound", credentialId);
      throw new NoSuchCredentialException();
    }
    
    return editorFactory.newEditor(credential);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequest createRequest(
      CredentialRequestEditor editor, ProtectionParameters protection, 
      Errors errors) throws NoSuchGroupException, PassphraseException, 
      GroupAccessException, CredentialRequestException {
    try {
      CredentialRequest request = generator.generate(editor, protection);
      if (editor.getCredentialId() != null) {
        Credential credential = credentialRepository.findById(
            editor.getCredentialId());
        request.setCredential(credential);
      }
      request.setName(editor.getName());
      request.setNote(editor.getNote());
      request.setTags(tagService.resolve(editor.getTags()));
      return request;
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

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveRequest(CredentialRequest request, Errors errors) 
      throws GroupAccessException {
    try {
      requestRepository.add(request);
    }
    catch (OwnerAccessControlException ex) {
      errors.addError("owner", "groupAccessDenied", ex.getGroupName());
      throw new GroupAccessException(ex.getGroupName());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void downloadRequest(CredentialRequest request,
      FileDownloadResponse response) throws IOException {
    response.setFileName(normalizedFileName(request.getName(), SUFFIX));
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    Writer writer = response.getWriter();
    writer.write(request.getCertificationRequest().getContent());
    writer.flush();
  }

  /**
   * Creates a normalized file name from a base name and a suffix.
   * @param base base name
   * @param suffix suffix
   * @return file name
   */
  private String normalizedFileName(String base, String suffix) {
    return base.trim().replaceAll("\\.|\\s+", "_") + suffix;
  }

}
