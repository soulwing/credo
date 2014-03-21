/*
 * File created on Mar 21, 2014 
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
package org.soulwing.credo.domain;

import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.CredentialRequestBuilder;

/**
 * A {@link CredentialRequestBuilder} that builds a 
 * {@link CredentialRequestEntity} object.
 *
 * @author Carl Harris
 */
public class CredentialRequestEntityBuilder
    implements CredentialRequestBuilder {

  private final CredentialRequestEntity request =
      new CredentialRequestEntity();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestBuilder setSubject(X500Principal subject) {
    request.setSubject(subject.getName());
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestBuilder setPrivateKey(String privateKey) {
    CredentialKeyEntity key = new CredentialKeyEntity();
    key.setEncoded(privateKey);
    request.setPrivateKey(key);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestBuilder setCertificationRequest(
      String certificationRequest) {
    CredentialCertificationRequestEntity csr =
        new CredentialCertificationRequestEntity();
    csr.setEncoded(certificationRequest);
    request.setCertificationRequest(csr);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequest build() {
    return request;
  }

}
