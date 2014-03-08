/*
 * File created on Mar 8, 2014 
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilder;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link CertificateWrapper} that wraps a JCA {@link X509Certificate}.
 *
 * @author Carl Harris
 */
public class JcaX509CertificateWrapper implements CertificateWrapper {

  private final X509Certificate certificate;
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * Constructs a new instance.
   * @param certificate
   * @param objectBuilderFactory
   */
  public JcaX509CertificateWrapper(X509Certificate certificate,
      PemObjectBuilderFactory objectBuilderFactory) {
    this.certificate = certificate;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getSubject() {
    return certificate.getSubjectX500Principal();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getIssuer() {
    return certificate.getIssuerX500Principal();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigInteger getSerialNumber() {
    return certificate.getSerialNumber();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotBefore() {
    return certificate.getNotBefore();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotAfter() {
    return certificate.getNotAfter();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSelfSigned() {
    return getSubject().equals(getIssuer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() throws IOException {
    try {
      PemObjectBuilder objectBuilder = objectBuilderFactory.newBuilder();
      objectBuilder.setType("CERTIFICATE");
      objectBuilder.append(certificate.getEncoded());
      return objectBuilder.build().getEncoded();
    }
    catch (CertificateEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Certificate derive() {
    return certificate;
  }

}
