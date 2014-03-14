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

import java.security.PrivateKey;

import javax.crypto.SecretKey;

/**
 * A wrapper for a secret key object.
 *
 * @author Carl Harris
 */
public interface SecretKeyWrapper {

  /**
   * Tests whether this secret key was encrypted (wrapped) using a 
   * public key.
   * @return {@code true} if a private key is required to derive this
   *    secret key
   */
  boolean isPrivateKeyRequired();
  
  /**
   * Gets the private key to use in unwrapping this secret key
   * @return public key or {@code null} if none has been set
   */
  PrivateKey getPrivateKey();
  
  /**
   * Sets the private key to use in unwrapping this secret key
   * @param privateKey the private key to set
   */
  void setPrivateKey(PrivateKey privateKey);
  
  /**
   * Gets the content of this secret key in a suitable string encoding
   * (typically PEM).
   * @return secret key content
   */
  String getContent();
  
  /**
   * Derive the JCA {@link SecretKey} that corresponds to this wrapper.
   * @return JCA private key
   */
  SecretKey derive();

  /**
   * Derives and re-wraps the JCP {@link SecretKey} that corresponds to this
   * wrapper.
   * @return secret key wrapper
   */
  SecretKeyWrapper deriveWrapper();

}
