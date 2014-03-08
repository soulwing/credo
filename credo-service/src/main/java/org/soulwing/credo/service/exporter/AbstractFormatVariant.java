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

import org.soulwing.credo.service.ExportFormat;

/**
 * An abstract base for format variant implementations.
 *
 * @author Carl Harris
 */
public abstract class AbstractFormatVariant implements ExportFormat.Variant {

  private final String id;
  private final String name;
  private final String description;
  private final String suffix;
  
  /**
   * Constructs a new instance.
   * @param id variant identifier
   * @param suffix file name suffix
   */
  protected AbstractFormatVariant(String id, String suffix) {
    this.id = id;
    this.name = BundlePrefix.VARIANT_NAME + id;
    this.description = BundlePrefix.VARIANT_DESCRIPTION + id;
    this.suffix = suffix;
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
  public String getSuffix() {
    return suffix;
  }  
  
}
