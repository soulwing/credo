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


/**
 * A service that supports the Export Credential function.
 *
 * @author Carl Harris
 */
public interface ExportService {

  /**
   * Creates a new request to export a given credential.
   * @param credentialId persistent identifier of the credential to be
   *    exported
   * @return export request
   * @throws NoSuchCredentialException if {@code credentialId} does not
   *    refer to an extant credential
   */
  ExportRequest newExportRequest(Long credentialId) 
      throws NoSuchCredentialException;
  
  /**
   * Performs the work required to export a credential.
   * @param request the export request to act upon
   * @return export preparation
   * @throws ExportException if an error occurs in performing the request
   * @throws PassphraseException if the credential requires a passphrase 
   *    but the provided credential was not provided or incorrect
   * @throws NoSuchGroupException the user identified in the request's
   *    protection parameters is not a member of the credential's owner group
   */
  ExportPreparation prepareExport(ExportRequest request)
      throws ExportException, PassphraseException, NoSuchGroupException;
  
}
