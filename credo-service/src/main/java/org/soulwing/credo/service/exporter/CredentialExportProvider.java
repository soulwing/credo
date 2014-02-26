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

import org.soulwing.credo.service.ExportFormat;

/**
 * A provider that knows how to support some export formats.
 *
 * @author Carl Harris
 */
public interface CredentialExportProvider {

  /**
   * Tests whether the receiver knows how to export in the the given format.
   * @param format the subject format
   * @return {@code true} if the receiver can export using {@code format}
   */
  boolean supports(ExportFormat format);
  
  /**
   * Creates a new exporter.
   * @return exporter
   */
  CredentialExporter newExporter();
  
}
