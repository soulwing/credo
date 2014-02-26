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
package org.soulwing.credo.service.exporter;

import java.io.IOException;
import java.util.List;

import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportFormat;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.archive.ArchiveBuilder;

/**
 * An exporter that exports in the {@link ExportFormat#PEM_ARCHIVE} format.
 *
 * @author Carl Harris
 */
public class PemArchiveExporter implements CredentialExporter {

  static final String KEY_ENTRY_NAME = "server.key";

  static final String CERT_ENTRY_NAME = "server.crt";

  static final String CA_CERTS_ENTRY_NAME = "server-ca.crt";

  static final String CONTENT_TYPE = "text/plain";
  
  static final String CHARACTER_ENCODING = "UTF-8";
  
  private final ArchiveBuilder archiveBuilder;
  
  /**
   * Constructs a new instance.
   * @param archiveBuilder
   */
  public PemArchiveExporter(ArchiveBuilder archiveBuilder) {
    this.archiveBuilder = archiveBuilder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportPreparation exportCredential(ExportRequest request)
      throws IOException, PassphraseException, ExportException {
    
    archiveBuilder.beginEntry(KEY_ENTRY_NAME, CHARACTER_ENCODING)    
      .addContent(request.getCredential().getPrivateKey().getContent())
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
        request.getFileName(), CONTENT_TYPE, 
        CHARACTER_ENCODING, archiveBuilder.build());
  }

}
