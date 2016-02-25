/*
 * File created on Mar 20, 2014 
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

import javax.security.auth.x500.X500Principal;

/**
 * A builder for a PKCS#10 certificate signing request.
 *
 * @author Carl Harris
 */
public interface CertificationRequestBuilder {

  /**
   * Sets the subject name for the certification request.
   * @param subject subject name
   * @return the receiver
   */
  CertificationRequestBuilder setSubject(X500Principal subject);
  
  /**
   * Sets the subject's public key.
   * @param publicKey public key associated with the subject
   * @return the receiver
   */
  CertificationRequestBuilder setPublicKey(PublicKeyWrapper publicKey);
  
  /**
   * Builds the certification request.
   * @param privateKey private key that will be used to sign the request
   * @return certification request
   * @throws CertificationRequestException as a wrapper for an exception that 
   *    while generating the certification request
   */
  CertificationRequestWrapper build(PrivateKeyWrapper privateKey) 
      throws CertificationRequestException;
  
}
