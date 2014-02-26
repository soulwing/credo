/*
 * File created on Feb 24, 2014 
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.service.exporter.CredentialExportProvider;
import org.soulwing.credo.service.exporter.CredentialExporter;

/**
 * A concrete {@link ExportService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteExportService implements ExportService {

  @Inject
  protected CredentialService credentialService;
  
  @Inject
  @Any
  protected Instance<CredentialExportProvider> exportProvider;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ExportRequest newExportRequest(Long credentialId)
      throws NoSuchCredentialException {
    return new ConcreteExportRequest(credentialService.findCredentialById(
        credentialId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportPreparation prepareExport(ExportRequest request)
      throws ExportException, PassphraseException {
    CredentialExportProvider provider = findProvider(request);
    
    CredentialExporter exporter = provider.getExporter();
    try {
      return exporter.exportCredential(request);      
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private CredentialExportProvider findProvider(ExportRequest request) {
    for (CredentialExportProvider provider : exportProvider) {
      if (provider.supports(request.getFormat())) {
        return provider;
      }
    }
    throw new IllegalArgumentException("unsupported format: " 
        + request.getFormat());
  }

}
