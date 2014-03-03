/*
 * File created on Mar 2, 2014 
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
package org.soulwing.credo.service.crypto;

/**
 * A password encryption service.
 * <p>
 * An implementation of this service encrypts passwords using an irreversible
 * algorithm and validates presented passwords.
 * 
 * @author Carl Harris
 */
public interface PasswordEncryptionService {

  /**
   * Encrypts a password.
   * @param password the password to encrypt
   * @return printable encrypted password string
   */
  String encrypt(char[] password);
  
  /**
   * Validates that a password presented by a user matches the given 
   * encrypted password.
   * @param presented password presented by the user
   * @param expected expected password (encrypted) 
   * @return {@code true} if the passwords match
   */
  boolean validate(char[] presented, String expected);
  
}
