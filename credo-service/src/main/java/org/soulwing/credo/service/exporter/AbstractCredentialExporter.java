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


/**
 * An abstract base for {@link CredentialExporter} implementations.
 *
 * @author Carl Harris
 */
abstract class AbstractCredentialExporter implements CredentialExporter {

  private final String id;
  private final String name;
  private final String description;
  private final boolean passphraseRequired;
  private final boolean defaultFormat;
  
  /**
   * Constructs a new instance.
   * @param id format identifier
   * @param passphraseRequired
   */
  protected AbstractCredentialExporter(String id, 
      boolean passphraseRequired) {
    this(id, passphraseRequired, false);
  }

  /**
   * Constructs a new instance.
   * @param id format identifier
   * @param passphraseRequired
   * @param defaultFormat
   */
  protected AbstractCredentialExporter(String id, 
      boolean passphraseRequired, boolean defaultFormat) {
    this.id = id;
    this.name = BundlePrefix.FORMAT_NAME + id;
    this.description = BundlePrefix.FORMAT_DESCRIPTION + id;
    this.passphraseRequired = passphraseRequired;
    this.defaultFormat = defaultFormat;
  }
  

  /**
   * {@inheritDoc}
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    return passphraseRequired;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDefault() {    
    return defaultFormat;
  }  

}
