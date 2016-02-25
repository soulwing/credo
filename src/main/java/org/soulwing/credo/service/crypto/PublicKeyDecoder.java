/*
 * File created on Mar 14, 2014 
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
 * A decoder for a PEM-encoded public key.
 *
 * @author Carl Harris
 */
public interface PublicKeyDecoder {

  /**
   * Decodes a PEM-encoded public key.
   * @param encoded
   * @return public key
   * @throws IllegalArgumentException if the encoded content does not contain
   *    a public key
   */
  PublicKeyWrapper decode(String encoded);

}
