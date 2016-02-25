/*
 * File created on Feb 25, 2014 
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
package org.soulwing.credo.service.exporter.archive;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.archive.ArchiveBuilder;
import org.soulwing.credo.service.credential.ExportException;
import org.soulwing.credo.service.credential.ExportFormat;
import org.soulwing.credo.service.credential.ExportPreparation;
import org.soulwing.credo.service.credential.ExportRequest;
import org.soulwing.credo.service.crypto.PKCS8EncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.AbstractVariantExporter;
import org.soulwing.credo.service.exporter.ConcreteExportPreparation;

/**
 * An exporter that exports in the {@link ExportFormat#PEM_ARCHIVE} format.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class PemArchiveExporter 
    extends AbstractVariantExporter<PemArchiveVariant> {

  static final String ID = "Archive";
  
  static final String KEY_ENTRY_NAME = "server.key";

  static final String CERT_ENTRY_NAME = "server.crt";

  static final String CA_CERTS_ENTRY_NAME = "server-ca.crt";

  static final String CHARACTER_ENCODING = "UTF-8";
    
  @Inject
  protected PKCS8EncryptionService pkcs8EncryptionService;
  
  /**
   * Constructs a new instance.
   */
  public PemArchiveExporter() {
    super(ID, false);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ExportPreparation exportCredential(ExportRequest request, 
      PrivateKeyWrapper privateKey) throws IOException, PassphraseException, 
      ExportException {
    
    if (request.getExportPassphrase() != null 
        && !request.getExportPassphrase().isEmpty()) {
      privateKey = pkcs8EncryptionService.encrypt(privateKey, 
          request.getExportPassphrase());
    }
    
    PemArchiveVariant variant = findVariant(request.getVariant());
    
    ArchiveBuilder archiveBuilder = variant.newArchiveBuilder();
    archiveBuilder.beginEntry(KEY_ENTRY_NAME, CHARACTER_ENCODING)    
      .addContent(privateKey.getContent())
      .endEntry();

    List<? extends CredentialCertificate> certificates = 
        request.getCredential().getCertificates();
    if (certificates.size() > 0) {
      archiveBuilder.beginEntry(CERT_ENTRY_NAME, CHARACTER_ENCODING)
          .addContent(certificates.get(0).getContent())
          .endEntry();
      if (certificates.size() > 1) {
        archiveBuilder.beginEntry(CA_CERTS_ENTRY_NAME, CHARACTER_ENCODING);
        for (int i = 1; i < certificates.size(); i++) {
          archiveBuilder.addContent(certificates.get(i).getContent());
        }
        archiveBuilder.endEntry();
      }
    }

    return new ConcreteExportPreparation(
        request.getSuffixedFileName(variant.getSuffix()), 
        variant.getContentType(), 
        CHARACTER_ENCODING, archiveBuilder.build());
  }

}
