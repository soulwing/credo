/*
 * File created on Mar 9, 2014 
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

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * An {@link ArchiveBuilder} that builds TAR.GZ archives.
 *
 * @author Carl Harris
 */
public class TarGzipArchiveBuilder extends AbstractArchiveBuilder {
  
  private static final int USER_ID = 0;
  private static final int GROUP_ID = 0;
  private static final String USER_NAME = "root";
  private static final String GROUP_NAME = "daemon";
  private static final int MODE = TarArchiveEntry.DEFAULT_FILE_MODE & 0777400;

  private final ArchiveOutputStream tos;
  
  private TarArchiveEntry entry;

  public TarGzipArchiveBuilder() {
    try {
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    try {
      tos = new TarArchiveOutputStream(new GzipCompressorOutputStream(buffer));
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  protected void onBeginEntry(String name, String charset) throws IOException {
    entry = new TarArchiveEntry(name);
    entry.setIds(USER_ID, GROUP_ID);
    entry.setNames(USER_NAME, GROUP_NAME);
    entry.setMode(MODE);
  }

  @Override
  protected void beforeWriteEntry(byte[] content) throws IOException {
    entry.setSize(content.length);
    tos.putArchiveEntry(entry);
  }

  @Override
  protected void onEndEntry() throws IOException {
    tos.closeArchiveEntry();
  }

  @Override
  protected void onBuild() throws IOException {
    tos.close();
  }

  @Override
  protected OutputStream getOutputStream() {
    return tos;
  }

}
