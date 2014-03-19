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
import org.soulwing.credo.Password;

/**
 * A service that imports credentials using the contents of uploaded files.
 *
 * @author Carl Harris
 */
@Local
public interface ImportService {

  /**
   * Prepares the contents of a collection of files for import as a credential.
   * @param files files whose contents will be imported
   * @param errors an errors object that will be updated during the
   *   import as necessary
   * @param passphrase passphrase for the private key (which may be null or
   *   empty)
   * @return imported credential details
   * @throws PassphraseException if a passphrase is required and was not
   *    provided or is incorrect
   * @throws ImportException if a validation error occurs; warnings do
   *    not result in an exception
   */
  ImportDetails prepareImport(List<FileContentModel> files, 
      Errors errors, Password passphrase) throws PassphraseException, 
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
   * @param errors an errors object that will be updated if a recoverable
   *    error occurs
   * @throws ImportException if a recoverable error occurs
   */
  void saveCredential(Credential credential, Errors errors) 
      throws ImportException;

}
