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

import java.math.BigInteger;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialCertificateBuilder;

/**
 * A {@link CredentialCertificateBuilder} that builds a
 * {@link CredentialCertificateEntity}.
 *
 * @author Carl Harris
 */
public class CredentialCertificateEntityBuilder 
    implements CredentialCertificateBuilder {

  private final CredentialCertificateEntity certificate =
      new CredentialCertificateEntity();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setSubject(X500Principal name) {
    certificate.setSubject(name.getName());
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setIssuer(X500Principal name) {
    certificate.setIssuer(name.getName());
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setSerialNumber(BigInteger serialNumber) {
    certificate.setSerialNumber(serialNumber.toString());
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setNotBefore(Date notBefore) {
    certificate.setNotBefore(notBefore);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setNotAfter(Date notAfter) {
    certificate.setNotAfter(notAfter);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificateBuilder setContent(String content) {
    certificate.setEncoded(content);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificate build() {
    return certificate;
  }

}
