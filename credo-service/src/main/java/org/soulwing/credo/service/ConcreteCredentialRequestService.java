/*
 * File created on Mar 20, 2014 
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
import java.io.Writer;
import java.util.List;

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
import org.soulwing.credo.service.request.CredentialRequestEditorFactory;
import org.soulwing.credo.service.request.CredentialRequestGenerator;

/**
 * A concrete {@link CredentialRequestService} as a singleton session bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteCredentialRequestService implements CredentialRequestService {

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
  protected UserContextService userContextService;
  
  @Inject
  protected TagService tagService;

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequestDetail findRequestById(Long id)
      throws NoSuchCredentialException {
    
    CredentialRequest request = requestRepository.findById(id);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    
    Credential credential = credentialRepository.findByRequestId(id);
    
    CredentialRequestWrapper detail = new CredentialRequestWrapper(request);
    detail.setCredentialCreated(credential != null);
    
    return detail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public List<CredentialRequest> findAllRequests() {
    return requestRepository.findAllByLoginName(
        userContextService.getLoginName());
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
  public void saveRequest(CredentialRequest request) {
    requestRepository.add(request);
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
   * {@inheritDoc}
   */
  @Override
  public void downloadRequest(Long requestId, FileDownloadResponse response)
      throws NoSuchCredentialException, IOException {
    CredentialRequest request = requestRepository.findById(requestId);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    downloadRequest(request, response);
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeRequest(Long id) {
    Credential credential = credentialRepository.findByRequestId(id);
    if (credential != null) {
      credential.setRequest(null);
      credentialRepository.update(credential);
    }
    requestRepository.remove(id, credential == null);
  }
  
  
}
