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

import javax.ejb.Local;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;

/**
 * A service that provides support for creating {@link CredentialRequest}
 * objects via a user interaction.
 *
 * @author Carl Harris
 */
@Local
public interface CreateCredentialRequestService {

  /**
   * Creates an editor for a request that is based on an existing
   * credential.
   * @param credentialId unique identifier of the subject credential
   * @param errors an errors object that will be updated if an error occurs
   * @return editor whose state reflects the subject credential
   * @throws NoSuchCredentialException if there exists no credential with the
   *    specified identifier
   */
  CredentialRequestEditor createEditor(Long credentialId, Errors errors) 
      throws NoSuchCredentialException;
  
  /**
   * Create a request using the contents of the specified editor.
   * @param editor editor which specifies the properties of the request
   * @param protection parameters which will be used to protect the private
   *    key for the request
   * @param errors an errors object that will be updated if an error occurs
   * @return request
   * @throws NoSuchGroupException if the group specified in the protection
   *    parameters does not exist
   * @throws PassphraseException if the password specified in the protection
   *    parameters is null, empty, or incorrect
   * @throws GroupAccessException if the logged in user is not a member of the
   *    group specified in the protection parameters 
   * @throws CredentialRequestException if some other error occurs in creating 
   *    the request
   */
  CredentialRequest createRequest(CredentialRequestEditor editor, 
      ProtectionParameters protection, Errors errors)
      throws NoSuchGroupException, PassphraseException, GroupAccessException,
      CredentialRequestException;

  /**
   * Saves the given (transient) request making it persistent.
   * @param request the subject request
   * @param errors an errors object that will be updated if an errors occurs
   * @throws GroupAccessException if the logged in user is not a member of the
   *    group specified as the request owner 
   */
  void saveRequest(CredentialRequest request, Errors errors) 
      throws GroupAccessException;
  
  /**
   * Transfers a request to a client via the given response object.
   * @param request the subject request
   * @param response response object that will be used to transfer the
   *    request content
   * @throws IOException if an error occurs in transferring the 
   *    request content to the remote client
   */
  void downloadRequest(CredentialRequest request,
      FileDownloadResponse response) throws IOException;
  

}
