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
package org.soulwing.credo.service;

import java.io.IOException;
import java.io.InputStream;

import org.soulwing.credo.Credential;

/**
 * A builder for a {@link Credential}.
 *
 * @author Carl Harris
 */
public interface CredentialImporter extends ImportPreparation {

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
   * Validates the credential content.
   * @param errors errors object that will be updated with errors/warnings
   *    during validation
   * @throws ImportException if a validation error occurs
   */
  void validate(Errors errors) throws ImportException;
  
  /**
   * Creates a {@link Credential} containing the uploaded key and certificate 
   * chain.
   * @return transient credential object
   */
  Credential build();
  
}
