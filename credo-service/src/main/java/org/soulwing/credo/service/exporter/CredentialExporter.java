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

import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.PassphraseException;

/**
 * An object that performs an export request for a particular export format.
 *
 * @author Carl Harris
 */
public interface CredentialExporter {

  /**
   * Performs the export of a credential represented by the given request.
   * @param request the subject request
   * @return prepared export
   * @throws IOException
   * @throws ExportException
   * @throws PassphraseException if a passphrase is required for the export
   *    and is not provided or is incorrect 
   */
  ExportPreparation exportCredential(ExportRequest request) 
      throws IOException, PassphraseException, ExportException;
  
}
