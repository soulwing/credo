/*
 * File created on Mar 19, 2014 
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

import org.soulwing.credo.CredentialSigningRequest;


/**
 * A service that supports the creation and manipulation of signing requests.
 *
 * @author Carl Harris
 */
public interface SigningRequestService {

  /**
   * Creates an editor for a signing request that is based on an existing
   * credential.
   * @param credentialId unique identifier of the subject credential
   * @param errors an errors object that will be updated if an error occurs
   * @return editor whose state reflects the subject credential
   * @throws NoSuchCredentialException if there exists no credential with the
   *    specified identifier
   */
  CredentialEditor createEditor(Long credentialId, Errors errors) 
      throws NoSuchCredentialException;
  
  /**
   * Create a signing request using the contents of the specified editor.
   * @param editor editor which specifies the properties of the request
   * @param protection parameters which will be used to protect the private
   *    key for the signing request
   * @param errors an errors object that will be updated if an error occurs
   * @return signing request
   * @throws PassphraseException if the password specified in the protection
   *    parameters is null, empty, or incorrect
   * @throws GroupAccessException if the logged in user is not a member of the
   *    group specified in the protection parameters 
   * @throws SigningRequestException if some other error occurs in creating 
   *    the signing request
   */
  CredentialSigningRequest createSigningRequest(CredentialEditor editor, 
      ProtectionParameters protection, Errors errors)
      throws PassphraseException, GroupAccessException,
      SigningRequestException;

  /**
   * Saves the given (transient) signing request making it persistent.
   * @param signingRequest the subject signing request
   */
  void saveSigningRequest(CredentialSigningRequest signingRequest);
  
  /**
   * Transfers a signing request to a client via the given response object.
   * @param signingRequest the subject signing request
   * @param response response object that will be used to transfer the
   *    signing request content
   * @throws IOException if an error occurs in transferring the signing 
   *    request content to the remote client
   */
  void downloadSigningRequest(CredentialSigningRequest signingRequest,
      FileDownloadResponse response) throws IOException;
  
}
