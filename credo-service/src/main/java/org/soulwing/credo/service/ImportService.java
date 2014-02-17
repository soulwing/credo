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
import java.util.Set;

import javax.ejb.Local;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;

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
   * @return prepared credential content
   * @throws ImportException if a validation error occurs; warnings do
   *    not result in an exception
   */
  ImportPreparation prepareImport(List<FileContentModel> files, 
      Errors errors) throws ImportException;
  
  /**
   * Creates the credential from the prepared imported contents. 
   * @param preparation prepared contents
   * @param errors an errors object that will be updated during 
   *   credential validation and creation
   * @return fully validated credential
   * @throws ImportException if the credential had one or more validation 
   *    errors/warnings
   * @throws PassphraseException to indicate that a provided passphrase was
   *    not correct
   */
  Credential createCredential(ImportPreparation preparation, Errors errors)
      throws ImportException, PassphraseException;
  
  /**
   * Save the given (transient) credential making it persistent.
   * @param credential the credential to save
   * @param errors an errors object that will be updated if a recoverable
   *    error occurs
   * @return credential the imported credential
   * @throws ImportException if a recoverable error occurs
   */
  void saveCredential(Credential credential, Errors errors) 
      throws ImportException;

  /**
   * Resolves an array of textual tag representations into a set of tag
   * entities.
   * @param tokens the array of tokens to resolve
   * @return set of tags
   */
  Set<? extends Tag> resolveTags(String[] tokens);

}
