/*
 * File created on Feb 19, 2014 
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

/**
 * A wrapper for a private key object implementation.
 *
 * @author Carl Harris
 */
public interface PrivateKeyWrapper {

  /**
   * Tests whether this private key is protected.
   * @return {@code true} if the key requires a protection parameter.
   */
  boolean isProtected();
  
  /**
   * Gets the protection parameter.
   * @return protection parameter or {@code null} if none has been set
   */
  Object getProtectionParameter();
  
  /**
   * Sets the protection parameter.
   * @param parameter the protection parameter to set
   */
  void setProtectionParameter(Object parameter);
  
  /**
   * Gets the content of this private key in a suitable string encoding
   * (typically PEM).
   * @return private key content
   */
  String getContent();
  
  /**
   * Derive the JCA {@link PrivateKey} that corresponds to this wrapper.
   * @return JCA private key
   * @throws IncorrectPassphraseException is a passphrase is required and
   *    is not provided or is incorrect
   */
  PrivateKey derive();
 
  /**
   * Derives and re-wraps the JCP {@link PrivateKey} that corresponds to this
   * wrapper.
   * @return 
   */
  PrivateKeyWrapper deriveWrapper();
  
}
