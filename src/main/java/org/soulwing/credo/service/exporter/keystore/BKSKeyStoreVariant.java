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
package org.soulwing.credo.service.exporter.keystore;

import javax.enterprise.context.ApplicationScoped;

import org.soulwing.credo.service.exporter.CredentialExporter;

/**
 * A {@link CredentialExporter} that exports a JKS key store.
 * 
 * @author Carl Harris
 */
@ApplicationScoped
public class BKSKeyStoreVariant extends AbstractKeyStoreVariant {

  private static final String TYPE = "BKS";
  private static final String CONTENT_TYPE = "application/octet-stream";
  private static final String SUFFIX = ".bks";

  /**
   * Constructs a new instance.
   */
  public BKSKeyStoreVariant() {
    super(TYPE, CONTENT_TYPE, SUFFIX);
  }
  
}