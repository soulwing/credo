/*
 * File created on Feb 16, 2014 
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
package org.soulwing.credo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialCertificateBuilder;

/**
 * A {@link CredentialImporter} that knows how to load PEM files.
 *
 * @author Carl Harris
 */
public class BcCredentialImporter implements CredentialImporter {
  
  private final List<Object> objects = new ArrayList<Object>();
  
  private final CredentialBuilderFactory credentialBuilderFactory;
  
  private char[] passphrase;  
  private Object privateKey;
  private X509CertificateHolder certificate;
  private List<X509CertificateHolder> chain; 
  
  /**
   * Constructs a new instance.
   * @param credentialBuilderFactory
   */
  public BcCredentialImporter(
      CredentialBuilderFactory credentialBuilderFactory) {
    this.credentialBuilderFactory = credentialBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadFile(InputStream inputStream) 
      throws IOException, NoContentException {
    List<Object> objects = BcPemUtil.readAllObjects(inputStream);
    if (objects.isEmpty()) {
      throw new NoContentException();
    }
    this.objects.addAll(objects);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Errors errors) throws ImportException,
      PassphraseException {

    privateKey = findPrivateKey(errors);
    
    certificate = findSubjectCertificate(
        extractPrivateKey(privateKey, errors), errors);
        
    chain = findAuthorityChain(certificate, errors);    
  }

  private Object findPrivateKey(Errors errors) throws ImportException {
    Object key = findPrivateKeyObject(null);
    if (key == null) {
      errors.addError("importNoPrivateKey");
    }
    else if (findPrivateKeyObject(key) != null) {
      errors.addError("importMultiplePrivateKeys");
    }
    if (errors.hasErrors()) {
      throw new ImportException();
    }
    return key;
  }

  private Object findPrivateKeyObject(Object key)  {
    for (Object obj : objects) {
      if (key != null && obj == key) continue;
      if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
        return obj;
      }
      else if (obj instanceof PEMKeyPair) {
        return obj;
      }
    }
    return null;
  }

  private RSAPrivateCrtKeyParameters extractPrivateKey(Object key, 
      Errors errors) throws ImportException {
    try {
      AsymmetricKeyParameter derivedKey = BcPemUtil.extractPrivateKey(
          BcPemUtil.extractPrivateKeyInfo(key, passphrase));
      if (!(derivedKey instanceof RSAPrivateCrtKeyParameters)) {
        errors.addError("importUnsupportedKeyType");
        throw new ImportException();
      }
      return (RSAPrivateCrtKeyParameters) derivedKey;
    }
    catch (IllegalArgumentException ex) {
      throw new PassphraseException();
    }
  }

  private X509CertificateHolder findSubjectCertificate(
      RSAPrivateCrtKeyParameters privateKey, Errors errors) 
          throws ImportException {
    for (Object obj : objects) {
      if (obj instanceof X509CertificateHolder) {
        X509CertificateHolder cert = (X509CertificateHolder) obj;
        AsymmetricKeyParameter key = BcPemUtil.createPublicKey(
            cert.getSubjectPublicKeyInfo());
        if (key instanceof RSAKeyParameters) {
          RSAKeyParameters publicKey = (RSAKeyParameters) key;
          if (publicKey.getModulus().equals(privateKey.getModulus())) {
            if (publicKey.getExponent().equals(
                privateKey.getPublicExponent())) {
              return cert;
            }
          }
        }
      }
    }
    errors.addError("importNoSubjectCertificate");
    throw new ImportException();
  }
  
  private List<X509CertificateHolder> findAuthorityChain(
      X509CertificateHolder certificate, Errors errors) {
    
    List<X509CertificateHolder> chain = new ArrayList<>();
    X509CertificateHolder issuerCert = findIssuerCertificate(certificate);
    while (issuerCert != null
        && !issuerCert.getSubject().equals(issuerCert.getIssuer())) {
      chain.add(issuerCert);
      issuerCert = findIssuerCertificate(issuerCert);
    }
    if (issuerCert != null) {
      chain.add(issuerCert);
    }
    if (issuerCert == null) {
      errors.addWarning("importIncompleteTrustChain");
    }
    return chain;
  }

  private X509CertificateHolder findIssuerCertificate(
      X509CertificateHolder subjectCert) {
    for (Object obj : objects) {
      if (obj instanceof X509CertificateHolder) {
        X509CertificateHolder cert = (X509CertificateHolder) obj;
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
  public Credential build() {
    CredentialBuilder builder = credentialBuilderFactory.newCredentialBuilder();
    builder.setPrivateKey(createPrivateKeyContent(privateKey));
    builder.setCertificate(createCertificate(certificate));
    for (X509CertificateHolder authority : chain) {
      builder.addAuthorityCertificate(createCertificate(authority));
    }
    return builder.build();
  }

  private CredentialCertificate createCertificate(
      X509CertificateHolder certificate) {
    CredentialCertificateBuilder builder = 
        credentialBuilderFactory.newCertificateBuilder();
    builder.setSubject(new X500Principal(certificate.getSubject().toString()));
    builder.setIssuer(new X500Principal(certificate.getIssuer().toString()));
    builder.setSerialNumber(certificate.getSerialNumber());
    builder.setNotBefore(certificate.getNotBefore());
    builder.setNotAfter(certificate.getNotAfter());
    builder.setContent(createCertificateContent(certificate));
    return builder.build();    
  }

  private String createPrivateKeyContent(Object privateKey) {
    StringWriter writer = new StringWriter();
    try {
      BcPemUtil.writePrivateKey(privateKey, passphrase, writer);
      return writer.toString();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private String createCertificateContent(
      X509CertificateHolder certificate) {
    StringWriter writer = new StringWriter();
    try {
      BcPemUtil.writeCertificate(certificate, writer);
      return writer.toString();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  @Override
  public boolean isPassphraseRequired() {
    for (Object obj : objects) {
      if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
        return true;
      }
    }
    return false;
  }

  @Override
  public char[] getPassphrase() {
    return passphrase;
  }

  @Override
  public void setPassphrase(char[] passphrase) {
    this.passphrase = passphrase;
  }

}
