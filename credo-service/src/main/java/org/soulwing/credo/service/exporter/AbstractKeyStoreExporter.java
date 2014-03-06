/*
 * File created on Mar 6, 2014 
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
package org.soulwing.credo.service.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import javax.annotation.PostConstruct;

import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;
import org.soulwing.credo.service.pem.bc.BcPemObjectFactory;

/**
 * An abstract base for {@link CredentialExporter} implementations that 
 * export a {@link KeyStore}.
 *
 * @author Carl Harris
 */
abstract class AbstractKeyStoreExporter implements CredentialExporter {
  
  private final String type;
  private final String contentType;
  private final String suffix;
  
  private CertificateFactory certificateFactory;
  
  /**
   * Constructs a new instance.
   * @param type key store type
   * @param contentType MIME content type
   * @param suffix file name suffix
   */
  public AbstractKeyStoreExporter(String type, String contentType,
      String suffix) {
    this.type = type;
    this.contentType = contentType;
    this.suffix = suffix;
  }

  /**
   * Initializes the receiver.
   */
  @PostConstruct
  public void init() {
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }    
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ExportPreparation exportCredential(ExportRequest request,
      PrivateKeyWrapper privateKey) throws IOException, PassphraseException,
      ExportException {
    if (request.getExportPassphrase() == null) {
      throw new PassphraseException();
    }
    try {
      KeyStore keyStore = KeyStore.getInstance(type);
      keyStore.load(null, null);
      
      keyStore.setKeyEntry(request.getFileName(), privateKey.derive(), 
          null, createCertificates(request));
      
      return new ConcreteExportPreparation(
          request.getSuffixedFileName(suffix), contentType, 
          createContent(request, keyStore));
    }
    catch (KeyStoreException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private Certificate[] createCertificates(ExportRequest request) {
    try {
      List<? extends CredentialCertificate> certificates = 
          request.getCredential().getCertificates();
      Certificate[] certs = new Certificate[certificates.size()];
      PemObjectFactory objectFactory = new BcPemObjectFactory();
      for (int i = 0; i < certs.length; i++) {
        PemObjectWrapper wrapper = 
            objectFactory.newPemObject(certificates.get(i).getContent());
        certs[i] = certificateFactory.generateCertificate(
            new ByteArrayInputStream(wrapper.getContent()));
      }
      return certs;
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
  }

  private byte[] createContent(ExportRequest request, KeyStore keyStore)
      throws KeyStoreException, IOException, NoSuchAlgorithmException,
      CertificateException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    keyStore.store(outputStream, request.getExportPassphrase().toCharArray());
    outputStream.close();    
    byte[] content = outputStream.toByteArray();
    return content;
  }

}
