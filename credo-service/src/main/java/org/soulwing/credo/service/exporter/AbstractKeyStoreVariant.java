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
 * An abstract base for {@link KeyStoreVariant}.
 *
 * @author Carl Harris
 */
abstract class AbstractKeyStoreVariant extends AbstractFormatVariant
    implements KeyStoreVariant {

  private final String type;
  private final String contentType;
  
  /** 
   * Constructs a new instance.
   * @param type JCA key store type
   * @param contentType MIME content type
   * @param suffix file name suffix
   */
  protected AbstractKeyStoreVariant(String type, String contentType,
      String suffix) {
    super(type, suffix);
    this.type = type;
    this.contentType = contentType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContentType() {
    return contentType;
  }

}
