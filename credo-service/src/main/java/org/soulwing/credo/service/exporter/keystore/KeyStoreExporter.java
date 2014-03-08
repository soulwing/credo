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
package org.soulwing.credo.service.exporter.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.crypto.CertificateWrapperFactory;
import org.soulwing.credo.service.crypto.KeyStoreBuilder;
import org.soulwing.credo.service.crypto.KeyStoreBuilderFactory;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.AbstractVariantExporter;
import org.soulwing.credo.service.exporter.ConcreteExportPreparation;
import org.soulwing.credo.service.exporter.CredentialExporter;

/**
 * An abstract base for {@link CredentialExporter} implementations that 
 * export a {@link KeyStore}.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class KeyStoreExporter extends AbstractVariantExporter<KeyStoreVariant> {
  
  private static final String ID = "KeyStore";

  @Inject
  protected CertificateWrapperFactory certificateFactory;
  
  @Inject
  protected KeyStoreBuilderFactory keyStoreBuilderFactory;
  
  /**
   * Constructs a new instance.
   */
  public KeyStoreExporter() {
    super(ID, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportPreparation exportCredential(ExportRequest request,
      PrivateKeyWrapper privateKey) throws IOException, PassphraseException,
      ExportException {
    Validate.notNull(request.getExportPassphrase());
    KeyStoreVariant variant = findVariant(request.getVariant());  
    try {
      KeyStoreBuilder builder = keyStoreBuilderFactory.newBuilder(
          variant.getType());
      builder.beginEntry(request.getFileName());
      builder.setPrivateKey(privateKey);
      builder.setPassphrase(request.getExportPassphrase());
      for (CredentialCertificate certificate : 
        request.getCredential().getCertificates()) {
        builder.addCertificate(certificateFactory.newCertificateWrapper(certificate));
      }
      builder.endEntry();
      
      return new ConcreteExportPreparation(
          request.getSuffixedFileName(variant.getSuffix()), 
          variant.getContentType(),
          builder.build(request.getExportPassphrase()));
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
