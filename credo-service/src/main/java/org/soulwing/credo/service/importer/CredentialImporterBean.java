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
package org.soulwing.credo.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.NoContentException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.TimeOfDayService;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.CredentialBag;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A concrete {@link CredentialImporter}.
 *
 * @author Carl Harris
 */
@Dependent
public class CredentialImporterBean implements ConfigurableCredentialImporter {

  @Inject
  protected CredentialBag bag;
  
  @Inject
  protected TimeOfDayService timeOfDayService;
  
  private PrivateKeyWrapper privateKey;
  private CertificateWrapper certificate;
  private List<CertificateWrapper> chain;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivateKey(PrivateKeyWrapper privateKey) {
    this.privateKey = privateKey;
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
  public ImportDetails validateAndImport(Password passphrase, Errors errors) 
      throws ImportException, PassphraseException {
    
    if (privateKey == null) {
      privateKey = bag.findPrivateKey();
      if (privateKey == null) {
        errors.addError("importNoPrivateKey");
        throw new ImportException();
      }
      
      if (bag.removeObject(privateKey) && bag.findPrivateKey() != null) {
        errors.addError("importMultiplePrivateKeys");
        throw new ImportException();
      }
    }
    
    if (certificate == null) {
      try {
        privateKey.setProtectionParameter(passphrase);
        certificate = bag.findSubjectCertificate(privateKey);
        if (certificate == null) {
          errors.addError("importNoSubjectCertificate");
          throw new ImportException();
        }
        if (certificate.getNotAfter().before(timeOfDayService.getCurrent())) {
          errors.addWarning("importExpiredSubjectCertificate");
        }
      }
      catch (IncorrectPassphraseException ex) {
        if (passphrase != null && !passphrase.isEmpty()) {
          errors.addError("password", "passwordIncorrect");
        }
        throw new PassphraseException();
      }
    }
    
    if (chain == null) {
      chain = bag.findAuthorityCertificates(certificate);
      if (chain.isEmpty()
          || !chain.get(chain.size() - 1).isSelfSigned()) {
        errors.addWarning("importIncompleteTrustChain");
      }
    }

    return new ImportDetailsBean(privateKey, certificate, chain);
  }

}
