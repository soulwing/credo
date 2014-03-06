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

import org.soulwing.credo.Password;


/**
 * An object that represents the prepared contents of files that represent
 * a credential to be imported.
 *
 * @author Carl Harris
 */
public interface ImportPreparation {
  
  /**
   * Tests whether this import preparation requires a passphrase prior to
   * validation.
   * @return {@code true} if a passphrase is required.
   */
  boolean isPassphraseRequired();
  
  /**
   * Gets the passphrase provided by the user.
   * @return passphrase
   */
  Password getPassphrase();
  
  /**
   * Sets the pasphrase provided by the user.
   * @param passphrase the passphrase to set
   */
  void setPassphrase(Password passphrase);

  /**
   * Gets the details of a fully-prepared import.
   * @return import details or {@code null} if the import is not fully
   *    prepared
   */
  ImportDetails getDetails();
  
}
