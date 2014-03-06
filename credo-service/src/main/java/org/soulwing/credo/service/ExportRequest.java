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

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;

/**
 * An object that describes a request to export a credential.
 *
 * @author Carl Harris
 */
public interface ExportRequest {

  /**
   * Gets the credential to be exported.
   * @return credential
   */
  Credential getCredential();
  
  /**
   * Gets the base filename to assign to the exported credential.
   * @return file name or {@code null} if none has been set
   */
  String getFileName();
  
  /**
   * Sets the base filename to assign to the exported credential.
   * @param fileName the file name to set
   */
  void setFileName(String fileName);
  
  /**
   * Gets the filename to assign to the exported credential with the
   * default suffix appended, if needed.
   * @param suffix the default suffix to append
   * @return filename
   * @throws IllegalStateException if no base filename has been set
   */
  String getSuffixedFileName(String suffix);
  
  /**
   * Gets the passphrase that will be used to protect the exported
   * credential.
   * @return export passphrase or {@code null} if none has been set
   */
  Password getExportPassphrase();
  
  /**
   * Sets the passphrase that will be used to protect the exported
   * credential.
   * @param exportPassphrase the export passphrase to set
   */
  void setExportPassphrase(Password exportPassphrase);

  /**
   * Gets the format for the exported credential.
   * @return export format
   */
  String getFormat();
  
  /**
   * Sets the format for the exported credential.
   * @param format the export format to set
   */
  void setFormat(String format);

  /**
   * Gets the protection parameters assigned to this request.
   * @return parameters object or {@code null} if none has been set
   */
  ProtectionParameters getProtectionParameters();
  
  /**
   * Sets the protection parameters assigned to this request.
   * @param parameters the parameters object to set
   */
  void setProtectionParameters(ProtectionParameters parameters);
  
}
