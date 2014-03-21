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

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.SigningRequest;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.SigningRequestRepository;
import org.soulwing.credo.service.request.SigningRequestEditorFactory;
import org.soulwing.credo.service.request.SigningRequestGenerator;

/**
 * A concrete {@link SigningRequestService} as a singleton session bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteSigningRequestService implements SigningRequestService {

  static final String CONTENT_TYPE = "application/pkcs10";
  
  static final String CHARACTER_ENCODING = "US-ASCII";
  
  static final String SUFFIX = ".csr";
  
  @Inject
  protected CredentialRepository credentialRepository;

  @Inject
  protected SigningRequestEditorFactory editorFactory;
  
  @Inject
  protected SigningRequestGenerator generator;
  
  @Inject
  protected SigningRequestRepository requestRespository;


  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public SigningRequestEditor createEditor(Long credentialId, Errors errors)
      throws NoSuchCredentialException {
    
    Credential credential = credentialRepository.findById(credentialId);
    if (credential == null) {
      errors.addError("credentialId", "credentialNotFound", credentialId);
      throw new NoSuchCredentialException();
    }
    
    return editorFactory.newEditor(credential);
  }

  @Override
  public SigningRequest createSigningRequest(
      SigningRequestEditor editor, ProtectionParameters protection, 
      Errors errors) throws NoSuchGroupException, PassphraseException, 
      GroupAccessException, SigningRequestException {
    try {
      return generator.generate(editor, protection, errors);
    }
    catch (UserAccessException ex) {
      throw new PassphraseException();
    }
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveSigningRequest(SigningRequest signingRequest) {
    requestRespository.add(signingRequest);
  }

  @Override
  public void downloadSigningRequest(SigningRequest request,
      FileDownloadResponse response) throws IOException {
    response.setFileName(normalizedFileName(request.getName(), SUFFIX));
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(CHARACTER_ENCODING);
    Writer writer = response.getWriter();
    writer.write(request.getContent());
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
