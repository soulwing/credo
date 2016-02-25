/*
 * File created on Mar 8, 2014 
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.service.credential.ExportFormat;
import org.soulwing.credo.service.credential.ExportRequest;
import org.soulwing.credo.service.credential.ExportFormat.Variant;

/**
 * A concrete {@link CredentialExporterRegistry} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteCredentialExporterRegistry 
    implements CredentialExporterRegistry {

  @Inject
  protected Instance<CredentialExporter> exporters;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<ExportFormat> getFormats() {
    List<ExportFormat> formats = new ArrayList<>();
    Iterator<CredentialExporter> i = exporters.iterator();
    while (i.hasNext()) {
      formats.add(i.next());
    }
    Collections.sort(formats, new Comparator<ExportFormat>() {
      @Override
      public int compare(ExportFormat a, ExportFormat b) {
        return a.getName().compareTo(b.getName());
      } 
    });
    return formats;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportFormat getDefaultFormat() {
    Iterator<CredentialExporter> i = exporters.iterator();
    while (i.hasNext()) {
      CredentialExporter exporter = i.next();
      if (exporter.isDefault()) return exporter;
    }
    return exporters.iterator().next();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Variant> getVariants(String format) {
    return findFormat(format).getVariants();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialExporter findExporter(ExportRequest request) {
    return findFormat(request.getFormat());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialExporter findFormat(String format) {
    Iterator<CredentialExporter> i = exporters.iterator();
    while (i.hasNext()) {
      CredentialExporter exporter = i.next();
      if (exporter.getId().equals(format)) {
        return exporter;
      }
    }
    throw new IllegalArgumentException("unrecognized format: " 
        + format);
  }

}
