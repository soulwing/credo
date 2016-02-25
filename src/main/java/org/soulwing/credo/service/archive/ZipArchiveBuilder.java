/*
 * File created on Feb 26, 2014 
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
package org.soulwing.credo.service.archive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * An {@link ArchiveBuilder} that builds ZIP archives.
 *
 * @author Carl Harris
 */
public class ZipArchiveBuilder extends AbstractArchiveBuilder {

  private final ZipOutputStream zos = new ZipOutputStream(buffer);
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onBeginEntry(String name, String charset)
      throws IOException {
    ZipEntry entry = new ZipEntry(name);
    zos.putNextEntry(entry);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEndEntry() throws IOException {
    zos.closeEntry();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onBuild() throws IOException {
    zos.close();
  }

  @Override
  protected OutputStream getOutputStream() {
    return zos;
  }

}
