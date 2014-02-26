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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.ExportFormat;
import org.soulwing.credo.service.archive.ArchiveBuilderFactory;

/**
 * A provider the supports the {@link ExportFormat#PEM_ARCHIVE} format.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class PemArchiveExportProvider implements CredentialExportProvider {

  @Inject
  protected ArchiveBuilderFactory archiveBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supports(ExportFormat format) {
    return ExportFormat.PEM_ARCHIVE.equals(format);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialExporter newExporter() {
    return new PemArchiveExporter(archiveBuilderFactory.newBuilder());
  }

}
