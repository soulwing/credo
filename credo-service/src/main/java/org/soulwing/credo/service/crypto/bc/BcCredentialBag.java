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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.CredentialBag;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.UnsupportedKeyTypeException;

/**
 * A {@link CredentialBag} implementation based on Bouncy Castle.
 *
 * @author Carl Harris
 */
public class BcCredentialBag implements CredentialBag {

  private static final Logger logger = 
      LoggerFactory.getLogger(BcCredentialBag.class);
  
  private final List<BcWrapper> objects = new ArrayList<>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int addAllObjects(InputStream inputStream) throws IOException {
    List<BcWrapper> objects = new ArrayList<>();
    try (PEMParser parser = new PEMParser(
        new InputStreamReader(inputStream, "UTF-8"))) {
      Object obj = parser.readObject();
      while (obj != null) {
        if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
          objects.add(new BcPrivateKeyWrapper(obj));          
        }
        else if (obj instanceof PEMEncryptedKeyPair) {
          objects.add(new BcPrivateKeyWrapper(obj));
        }
        else if (obj instanceof PEMKeyPair) {
          objects.add(new BcPrivateKeyWrapper(obj));          
        }
        else if (obj instanceof X509CertificateHolder) {
          objects.add(new BcCertificateWrapper((X509CertificateHolder) obj));
        }
        else {
          logger.info("unrecognized object of type: {}", 
              obj.getClass().getName());
        }
        obj = parser.readObject();
      }
    }
    
    this.objects.addAll(objects);
    return objects.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper findPrivateKey() {
    for (BcWrapper obj : objects) {
      if (obj instanceof PrivateKeyWrapper) {
        return (PrivateKeyWrapper) obj;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CertificateWrapper findSubjectCertificate(
      PrivateKeyWrapper privateKey) {
    if (!(privateKey instanceof BcPrivateKeyWrapper)) {
      throw new IllegalArgumentException("private key not from this provider");
    }
    
    RSAPrivateCrtKeyParameters rsaParams = deriveRSAKeyParameters(privateKey);
    
    for (BcWrapper obj : objects) {
      if (obj instanceof BcCertificateWrapper) {
        BcCertificateWrapper wrapper = (BcCertificateWrapper) obj;
        if (wrapper.matches(rsaParams)) {
          return wrapper;
        }
      }
    }
    
    return null;
  }

  private RSAPrivateCrtKeyParameters deriveRSAKeyParameters(
      PrivateKeyWrapper privateKey) {
    
    AsymmetricKeyParameter params = 
        ((BcPrivateKeyWrapper) privateKey).derivePrivateKeyParameters();
    
    if (!(params instanceof RSAPrivateCrtKeyParameters)) {
      throw new UnsupportedKeyTypeException();
    }
    
    return (RSAPrivateCrtKeyParameters) params;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CertificateWrapper> findAuthorityCertificates(
      CertificateWrapper certificate) {
    List<CertificateWrapper> chain = new ArrayList<>();
    CertificateWrapper issuerCert = findIssuerCertificate(certificate);
    while (issuerCert != null && !issuerCert.isSelfSigned()) {
      chain.add(issuerCert);
      issuerCert = findIssuerCertificate(issuerCert);
    }
    if (issuerCert != null) {
      chain.add(issuerCert);
    }
    return chain;
  }

  private CertificateWrapper findIssuerCertificate(
      CertificateWrapper subjectCert) {
    for (BcWrapper obj : objects) {
      if (obj instanceof BcCertificateWrapper) {
        BcCertificateWrapper cert = (BcCertificateWrapper) obj;
        if (cert.getSubject().equals(subjectCert.getIssuer())) {
          return cert;
        }
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeObject(Object obj) {
    if (!(obj instanceof BcWrapper)) return false;
    return objects.remove(((BcWrapper) obj));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    for (BcWrapper obj : objects) {
      if (obj instanceof BcPrivateKeyWrapper) {
        if (((BcPrivateKeyWrapper) obj).isPassphraseRequired()) {
          return true;
        }
      }
    }
    return false;
  }

  
}
