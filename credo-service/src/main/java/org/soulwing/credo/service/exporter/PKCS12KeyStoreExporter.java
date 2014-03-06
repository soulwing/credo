/*
 * File created on Mar 6, 2014 
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

/**
 * A {@link CredentialExporter} that exports a PKCS12 key store.
 *
 * @author Carl Harris
 */
@ApplicationScoped
@ExportFormat(PKCS12KeyStoreExporter.TYPE)
public class PKCS12KeyStoreExporter extends AbstractKeyStoreExporter {

  public static final String TYPE = "PKCS12";
  public static final String CONTENT_TYPE = "application/pkcs12";
  public static final String SUFFIX = ".p12";
  
  /**
   * Constructs a new instance.
   */
  public PKCS12KeyStoreExporter() {
    super(TYPE, CONTENT_TYPE, SUFFIX);
  }
  
}
