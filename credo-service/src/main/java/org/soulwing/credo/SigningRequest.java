/*
 * File created on Mar 19, 2014 
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
package org.soulwing.credo;

/**
 * An abstract representation of an PKCS#10 certificate signing request.
 *
 * @author Carl Harris
 */
public interface SigningRequest {

  /**
   * Gets the friendly name associated with this signing request.
   * @return friendly name
   */
  String getName();
  
  /**
   * Gets the private key for this signing request.
   * @return private key
   */
  CredentialKey getPrivateKey();
  
  /**
   * Gets the PEM encoded content for this signing request.
   * @return PEM-encoded DER representation of a PKCS#10 Certificate
   *    Signing Request (CDR) 
   */
  String getContent();
  
}
