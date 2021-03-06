/*
 * File created on Mar 7, 2014 
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

import java.util.Collection;

import org.soulwing.credo.service.credential.ExportFormat;
import org.soulwing.credo.service.credential.ExportRequest;

/**
 * An object that keeps track of available {@link CredentialExporter} objects.
 *
 * @author Carl Harris
 */
public interface CredentialExporterRegistry {

  /**
   * Gets the collection of all available formats.
   * @return collection of formats
   */
  Collection<ExportFormat> getFormats();
  
  /**
   * Gets the default format.
   * @return default format
   */
  ExportFormat getDefaultFormat();
  
  /**
   * Gets the collection of supported variants for the given export format.
   * @param format the subject export format
   * @return format variants
   */
  Collection<ExportFormat.Variant> getVariants(String format);
  
  /**
   * Finds a format using it's unique identifier.
   * @param id identifier of the format to match
   * @return export format
   * @throws IllegalArgumentException if the specified format does not exist
   */
  ExportFormat findFormat(String id);
  
  /**
   * Finds an exporter that supports a export request.
   * @param request export request
   * @return exporter
   * @throws IllegalArgumentException if the given request cannot be
   *    satisfied
   */
  CredentialExporter findExporter(ExportRequest request);
  
}
