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
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
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

  private final List<X509CertificateHolder> chain = 
      new ArrayList<X509CertificateHolder>();
  
  private final CredentialBuilderFactory credentialBuilderFactory;
  
  private char[] passphrase;
  
  private Object privateKey;
  
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
    int count = 0;
    try (PEMParser parser = new PEMParser(
        new InputStreamReader(inputStream, "UTF-8"))) {
      Object obj = parser.readObject();
      while (obj != null) {
        count++;
        objects.add(obj);
        obj = parser.readObject();
      }
    }
    if (count == 0) {
      throw new NoContentException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Errors errors) throws ImportException,
      PassphraseException {
    privateKey = findPrivateKey(errors);
    
    X509CertificateHolder subjectCert = findSubjectCertificate(
        extractPrivateKey(extractPrivateKeyInfo(privateKey), errors), errors);
    chain.add(subjectCert);
    
    X509CertificateHolder issuerCert = findIssuerCertificate(subjectCert);
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
    
  }

  private Object findPrivateKey(Errors errors) 
      throws ImportException, PassphraseException {
    for (Object obj : objects) {
      if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
        return obj;
      }
      else if (obj instanceof PEMKeyPair) {
        return obj;
      }
    }
    errors.addError("importNoPrivateKey");
    throw new ImportException();
  }

  private PrivateKeyInfo extractPrivateKeyInfo(Object key) 
      throws PassphraseException {
    if (key instanceof PKCS8EncryptedPrivateKeyInfo) {
      return decryptPrivateKey((PKCS8EncryptedPrivateKeyInfo) key);
    }
    else if (key instanceof PEMKeyPair) {
      return ((PEMKeyPair) key).getPrivateKeyInfo();
    }
    else {
      throw new AssertionError("unexpected key type " 
            + key.getClass().getName());
    }
  }
  
  private RSAPrivateCrtKeyParameters extractPrivateKey(
      PrivateKeyInfo privateKeyInfo, Errors errors) throws ImportException {
    try {      
      AsymmetricKeyParameter privateKey = PrivateKeyFactory.createKey(
          privateKeyInfo);
      if (!(privateKey instanceof RSAPrivateCrtKeyParameters)) {
        errors.addError("importUnsupportedKeyType");
        throw new ImportException();
      }
      return (RSAPrivateCrtKeyParameters) privateKey;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private PrivateKeyInfo decryptPrivateKey(
      PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) 
      throws PassphraseException {
    try {
      return encryptedPrivateKeyInfo.decryptPrivateKeyInfo(
          createPrivateKeyDecryptor());
    }
    catch (PKCSException ex) {
      throw new PassphraseException();
    }
  }

  private InputDecryptorProvider createPrivateKeyDecryptor() {
    try {
      assertPassphraseAvailable();
      return new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase);
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void assertPassphraseAvailable() {
    if (passphrase == null) {
      throw new AssertionError("passphrase is required and not set");
    }
  }
  
  private X509CertificateHolder findSubjectCertificate(
      RSAPrivateCrtKeyParameters privateKey, Errors errors) 
          throws ImportException {
    for (Object obj : objects) {
      if (obj instanceof X509CertificateHolder) {
        X509CertificateHolder cert = (X509CertificateHolder) obj;
        AsymmetricKeyParameter key = createPublicKey(
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
  
  private AsymmetricKeyParameter createPublicKey(
      SubjectPublicKeyInfo publicKeyInfo) {
    try {
      return PublicKeyFactory.createKey(publicKeyInfo);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
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
    builder.setPrivateKey(createPrivateKeyContent());
    Iterator<X509CertificateHolder> i = chain.iterator();
    builder.setCertificate(createCertificate(i.next()));
    while (i.hasNext()) {
      builder.addAuthorityCertificate(createCertificate(i.next()));
    }
    return builder.build();
  }

  private String createPrivateKeyContent() {
    StringWriter stringWriter = new StringWriter();
    try (PEMWriter pemWriter = new PEMWriter(stringWriter)) {
      PrivateKeyInfo privateKeyInfo = extractPrivateKeyInfo(privateKey);
      if (passphrase == null) {
        pemWriter.writeObject(privateKeyInfo);
      }
      else {
        PKCS8Generator generator = new PKCS8Generator(privateKeyInfo, 
            createPrivateKeyEncryptor());
        pemWriter.writeObject(generator.generate());
      }
      pemWriter.flush();
      return stringWriter.toString();
    }
    catch (PassphraseException ex) {
      throw new RuntimeException(ex);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
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

  private String createCertificateContent(
      X509CertificateHolder certificate) {
    StringWriter stringWriter = new StringWriter();
    try (PEMWriter pemWriter = new PEMWriter(stringWriter)) {
      pemWriter.writeObject(certificate);
      pemWriter.flush();
      return stringWriter.toString();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private OutputEncryptor createPrivateKeyEncryptor() {
    try {
      assertPassphraseAvailable();
      return new JceOpenSSLPKCS8EncryptorBuilder(
          PKCS8Generator.PBE_SHA1_3DES)
          .setPasssword(passphrase)
          .setIterationCount(100)
          .build();
    }
    catch (OperatorCreationException ex) {
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
