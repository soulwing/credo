/*
 * File created on Feb 18, 2014 
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
 * A builder for a {@link Credential}.
 *
 * @author Carl Harris
 */
public interface CredentialBuilder {

  /**
   * Sets the private key content for the credential.
   * @param content PEM-encoded private key content
   * @return the receiver
   */
  CredentialBuilder setPrivateKey(String content);
  
  /**
   * Sets the subject certificate for the credential.
   * @param certificate the subject certificate to set
   * @return the receiver
   */
  CredentialBuilder addCertificate(CredentialCertificate certificate);
  
  /**
   * Builds a new {@link Credential} according to the current state of the
   * receiver.
   * @return new (transient) credential object
   * @throws IllegalStateException if the state of the receiver is 
   *    invalid/inconsistent
   */
  Credential build();
   
}
