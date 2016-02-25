/*
 * File created on Feb 19, 2014 
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
package org.soulwing.credo.service.crypto.bc;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMWriter;
import org.soulwing.credo.service.crypto.CertificateWrapper;

/**
 * A {@link CertificateWrapper} implementation based on Bouncy Castle.
 * 
 * @author Carl Harris
 */
public class BcCertificateWrapper implements BcWrapper, CertificateWrapper {

  private final X509CertificateHolder certificate;

  /**
   * Constructs a new instance.
   * @param certificate certificate object
   */
  public BcCertificateWrapper(X509CertificateHolder certificate) {
    this.certificate = certificate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getSubject() {
    return new X500Principal(certificate.getSubject().toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getIssuer() {
    return new X500Principal(certificate.getIssuer().toString());
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
    return certificate.getSubject().equals(certificate.getIssuer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() throws IOException {
    StringWriter writer = new StringWriter();
    try (PEMWriter pemWriter = new PEMWriter(writer)) {
      pemWriter.writeObject(certificate);
      pemWriter.flush();
      return writer.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Certificate derive() {
    JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
    try {
      return converter.getCertificate(certificate);
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Tests whether a given RSA private key matches this certificate's
   * public key.
   * @param privateKey the private key to match
   * @return {@code true} if the keys match
   */
  public boolean matches(RSAPrivateCrtKeyParameters privateKey) {
    AsymmetricKeyParameter key = derivePublicKeyParameters();
    
    if (!(key instanceof RSAKeyParameters)) return false;
    
    RSAKeyParameters publicKey = (RSAKeyParameters) key;
    return publicKey.getModulus().equals(privateKey.getModulus())
        && publicKey.getExponent().equals(privateKey.getPublicExponent());
  }
  
  /**
   * Derives public key parameters from this certificate's subject public key.
   * @return public key parameters
   */
  private AsymmetricKeyParameter derivePublicKeyParameters() {
    try {
      return PublicKeyFactory.createKey(certificate.getSubjectPublicKeyInfo());
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
