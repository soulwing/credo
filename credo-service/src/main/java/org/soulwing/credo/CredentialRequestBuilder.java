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
package org.soulwing.credo;

import javax.security.auth.x500.X500Principal;

/**
 * A builder for a {@link CredentialRequest}.
 *
 * @author Carl Harris
 */
public interface CredentialRequestBuilder {

  /**
   * Sets the request's subject name.
   * @param subject the subject name to set
   * @return the receiver
   */
  CredentialRequestBuilder setSubject(X500Principal subject);
  
  /**
   * Sets the request's private key
   * @param privateKey the private key to set
   * @return the receiver
   */
  CredentialRequestBuilder setPrivateKey(String privateKey);
  
  /**
   * Sets the request's PKCS#10 certification request content.
   * @param certificationRequest PEM-encoded DER representation of the
   *    PKCS#10 certification request
   * @return the receiver
   */
  CredentialRequestBuilder setCertificationRequest(String certificationRequest);
  
  /**
   * Builds the signing request according to the receiver's configuration.
   * @return new (transient) signing request
   * @throws IllegalStateException if the state of the receiver is 
   *    invalid/inconsistent 
   */
  CredentialRequest build();
  
}
