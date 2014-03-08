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
package org.soulwing.credo.service.exporter.archive;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.archive.ArchiveBuilder;
import org.soulwing.credo.service.archive.ArchiveBuilderFactory;
import org.soulwing.credo.service.archive.Archiver;
import org.soulwing.credo.service.exporter.AbstractFormatVariant;


/**
 * A format variant that produces a ZIP archive. 
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ZipArchiveVariant extends AbstractFormatVariant 
    implements PemArchiveVariant {

  private static final String ID = "ZIP";
  
  private static final String SUFFIX = ".zip";
  
  private static final String CONTENT_TYPE = "application/zip";

  @Inject @Archiver(SUFFIX)
  protected ArchiveBuilderFactory archiveBuilderFactory;
  
  /**
   * Constructs a new instance.
   */
  public ZipArchiveVariant() {
    super(ID, SUFFIX, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContentType() {
    return CONTENT_TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ArchiveBuilder newArchiveBuilder() {
    return archiveBuilderFactory.newBuilder();
  }

}
