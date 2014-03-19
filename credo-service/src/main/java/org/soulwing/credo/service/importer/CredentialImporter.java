/*
 * File created on Feb 16, 2014 
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
package org.soulwing.credo.service.importer;

import java.io.IOException;
import java.io.InputStream;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.NoContentException;
import org.soulwing.credo.service.PassphraseException;

/**
 * A builder for a {@link Credential}.
 *
 * @author Carl Harris
 */
public interface CredentialImporter {

  /**
   * Loads a file containing content for a credential.
   * @param inputStream input stream representing the contents of the file
   * @throws NoContentException if the file doesn't contain anything
   *   that could be part of a credential
   * @throws IOException if an I/O error occurs
   */
  void loadFile(InputStream inputStream) 
      throws NoContentException, IOException;
  
  /**
   * Imports the credential content from the loaded files.
   * @param passphrase passphrase for the private key (which may be null or
   *    empty)
   * @param errors errors object that will be updated with errors/warnings
   *    as appropriate
   * @return details object that describes the imported credential
   * @throws ImportException if a validation error occurs
   * @throws PassphraseException if a passphrase is required and was not
   *    specified or was incorrect
   */
  ImportDetails validateAndImport(Password passphrase, Errors errors) 
      throws ImportException, PassphraseException;
  
}
