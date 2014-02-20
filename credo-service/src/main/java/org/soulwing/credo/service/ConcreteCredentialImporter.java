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
package org.soulwing.credo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialCertificateBuilder;
import org.soulwing.credo.service.x509.CertificateWrapper;
import org.soulwing.credo.service.x509.CredentialBag;
import org.soulwing.credo.service.x509.IncorrectPassphraseException;
import org.soulwing.credo.service.x509.PrivateKeyWrapper;

/**
 * A concrete {@link CredentialImporter}.
 *
 * @author Carl Harris
 */
public class ConcreteCredentialImporter implements CredentialImporter {

  private final CredentialBag bag;
  private final CredentialBuilderFactory credentialBuilderFactory;
  
  private char[] passphrase;
  private PrivateKeyWrapper privateKey;
  private CertificateWrapper certificate;
  private List<CertificateWrapper> chain;
  private ImportDetails details;
  
  /**
   * Constructs a new instance.
   * @param bag
   * @param credentialBuilderFactory
   */
  public ConcreteCredentialImporter(CredentialBag bag,
      CredentialBuilderFactory credentialBuilderFactory) {
    this.bag = bag;
    this.credentialBuilderFactory = credentialBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadFile(InputStream inputStream) throws NoContentException,
      IOException {
    if (bag.addAllObjects(inputStream) == 0) {
      throw new NoContentException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(Errors errors) throws ImportException {
    
    privateKey = bag.findPrivateKey();
    if (privateKey == null) {
      errors.addError("importNoPrivateKey");
      throw new ImportException();
    }
    
    if (bag.removeObject(privateKey) && bag.findPrivateKey() != null) {
      errors.addError("importMultiplePrivateKeys");
      throw new ImportException();
    }
    
    try {
      privateKey.setPassphrase(passphrase);
      certificate = bag.findSubjectCertificate(privateKey);
      if (certificate == null) {
        errors.addError("importNoSubjectCertificate");
        throw new ImportException();
      }
    }
    catch (IncorrectPassphraseException ex) {
      throw new PassphraseException();
    }
    
    chain = bag.findAuthorityCertificates(certificate);
    if (chain.isEmpty()
        || !chain.get(chain.size() - 1).isSelfSigned()) {
      errors.addWarning("importIncompleteTrustChain");
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential build() {
    try {
      CredentialBuilder builder = 
          credentialBuilderFactory.newCredentialBuilder();
      builder.setPrivateKey(privateKey.getContent());
      builder.setCertificate(createCertificate(certificate));
      for (CertificateWrapper authority : chain) {
        builder.addAuthorityCertificate(createCertificate(authority));
      }
      return builder.build();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private CredentialCertificate createCertificate(
      CertificateWrapper certificate) {
    try {
      CredentialCertificateBuilder builder =
          credentialBuilderFactory.newCertificateBuilder();
      builder.setSubject(certificate.getSubject());
      builder.setIssuer(certificate.getIssuer());
      builder.setSerialNumber(certificate.getSerialNumber());
      builder.setNotBefore(certificate.getNotBefore());
      builder.setNotAfter(certificate.getNotAfter());
      builder.setContent(certificate.getContent());
      return builder.build();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    return bag.isPassphraseRequired();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassphrase() {
    return passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassphrase(char[] passphrase) {
    this.passphrase = passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImportDetails getDetails() {
    if (certificate == null) return null;
    if (details == null) {
      details = new ConcreteImportDetails(certificate);
    }
    return details;
  }
  
}
