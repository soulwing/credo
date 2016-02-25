/*
 * File created on Mar 27, 2014 
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
package org.soulwing.credo.service.crypto.jca;

import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A strategy for decoding a PEM secret key object.
 * 
 * @author Carl Harris
 */
public interface JcaSecretKeyDecoderStrategy {

  /**
   * Decodes the given PEM object to a secret key, if possible.
   * @param object the object to decode
   * @return secret key or {@code null} if this strategy cannot decode the
   *    given object
   */
  SecretKeyWrapper decode(PemObjectWrapper object);
  
}
