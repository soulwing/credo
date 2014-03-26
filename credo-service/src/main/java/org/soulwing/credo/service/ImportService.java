/*
 * File created on Feb 13, 2014 
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

import java.util.List;

import javax.ejb.Local;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Password;

/**
 * A service that imports credentials using the contents of uploaded files.
 *
 * @author Carl Harris
 */
@Local
public interface ImportService {
  
  /**
   * Finds a request using its unique identifier.
   * @param id unique identifier of the subject request
   * @return request
   * @throws NoSuchCredentialException if no request exists with the given
   *    identifier
   */
  CredentialRequest findRequestById(Long id) 
      throws NoSuchCredentialException;
  
  /**
   * Prepares the contents of a collection of files for import as a credential.
   * @param files files whose contents will be imported
   * @param passphrase passphrase for the private key (which may be null or
   *   empty)
   * @param errors an errors object that will be updated during the
   *   import as necessary
   * @return imported credential details
   * @throws PassphraseException if a passphrase is required and was not
   *    provided or is incorrect
   * @throws ImportException if a validation error occurs; warnings do
   *    not result in an exception
   */
  ImportDetails prepareImport(List<FileContentModel> files, 
      Password passphrase, Errors errors) throws PassphraseException, 
      ImportException;

  /**
   * Prepares the contents of a credential request and a collection of files 
   * for import as a credential.
   * @param request subject credential request
   * @param files files whose contents will be imported
   * @param errors an errors object that will be updated during the
   *   import as necessary
   * @param protection protection parameters for the private key contained
   *    in {@code request}
   * @return imported credential details
   * @throws PassphraseException if a passphrase is required and was not
   *    provided or is incorrect
   * @throws GroupAccessException if the logged-in user is not a member of
   *    the group that owns {@code request}
   * @throws ImportException if a validation error occurs; warnings do
   *    not result in an exception
   */
  ImportDetails prepareImport(CredentialRequest request, 
      List<FileContentModel> files, ProtectionParameters protection, 
      Errors errors) throws PassphraseException, GroupAccessException,
      ImportException;

  /**
   * Protects the (private key of the) given credential. 
   * @param details prepared credential content
   * @param protection protection parameters
   * @param errors an errors object that will be updated to report any
   *    recoverable errors that occur
   * @throws NoSuchGroupException if the specified protection group 
   *    does not exist
   * @throws PassphraseException if the provided passphrase is incorrect
   * @throws GroupAccessException if the specified user is not a member of 
   *    the specified protection group 
   */
  Credential createCredential(ImportDetails details, 
      ProtectionParameters protection, Errors errors) 
      throws NoSuchGroupException, PassphraseException, GroupAccessException;
  
  /**
   * Save the given (transient) credential making it persistent.
   * @param credential the credential to save
   * @param removeRequest a flag that when {@code true} removes the request
   *    upon successfully saving the credential
   * @param errors an errors object that will be updated if a recoverable
   *    error occurs
   * @throws ImportException if a recoverable error occurs
   * @throws GroupAccessException if the logged-in user is no longer a member
   *    of the group that owns the request associated with {@code credential}
   */
  void saveCredential(Credential credential, boolean removeRequest, 
      Errors errors) throws ImportException, GroupAccessException;

}
