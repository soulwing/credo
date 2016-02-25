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

import org.soulwing.credo.Password;


/**
 * A service that encrypts private keys using PKCS8.
 *
 * @author Carl Harris
 */
public interface PKCS8EncryptionService {

  /** 
   * Encrypts the given private key.
   * <p>
   * If {@code privateKey} is already encrypted and its password is available
   * in its wrapper, the key will be decrypted and then encrypted using
   * {@code password}.
   * @param privateKey the subject private key
   * @param password passphrase that will be used to derive an encryption 
   *    key
   * @return encrypted private key
   */
  PrivateKeyWrapper encrypt(PrivateKeyWrapper privateKey,
      Password password);
  
}
