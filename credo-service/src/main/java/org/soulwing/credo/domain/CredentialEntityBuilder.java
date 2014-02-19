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
package org.soulwing.credo.domain;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialCertificate;

/**
 * A {@link CredentialBuilder} that builds a {@link CredentialEntity}.
 *
 * @author Carl Harris
 */
public class CredentialEntityBuilder implements CredentialBuilder {

  private final CredentialEntity credential = new CredentialEntity();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder setPrivateKey(String content) {
    CredentialKeyEntity privateKey = new CredentialKeyEntity();
    privateKey.setEncoded(content);
    credential.setPrivateKey(privateKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder setCertificate(
      CredentialCertificate certificate) {
    assertIsCertificateEntity(certificate);
    credential.setCertificate((CredentialCertificateEntity) certificate);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder addAuthorityCertificate(
      CredentialCertificate certificate) {
    assertIsCertificateEntity(certificate);
    credential.addAuthorityCertificate(
        (CredentialCertificateEntity) certificate);
    return this;
  }

  private void assertIsCertificateEntity(CredentialCertificate certificate) {
    if (!(certificate instanceof CredentialCertificateEntity)) {
      throw new IllegalArgumentException("illegal certificate type: "
          + certificate.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential build() {
    return credential;
  }

}
