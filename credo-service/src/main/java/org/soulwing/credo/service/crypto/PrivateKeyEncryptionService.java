/*
 * File created on Mar 3, 2014 
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
 * A service that encrypts (wraps) a private key using a secret key.
 *
 * @author Carl Harris
 */
public interface PrivateKeyEncryptionService {

  /**
   * Encrypts (wraps) a secret key using the given public key.
   * @param privateKey the key to encrypt
   * @param secretKey the secret key that will be used to wrap the private key 
   * @return encrypted (wrapped) private key
   */
  PrivateKeyWrapper encrypt(PrivateKeyWrapper privateKey, 
      SecretKeyWrapper secretKey);
  
}
