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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * An {@link ArchiveBuilder} that builds ZIP archives.
 *
 * @author Carl Harris
 */
public class ZipArchiveBuilder implements ArchiveBuilder {

  private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
  private final ZipOutputStream zos = new ZipOutputStream(bos);
  
  private String charset;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ArchiveBuilder beginEntry(String name, String charset)
      throws IOException {
    assertEntryOpenState(false);
    zos.putNextEntry(new ZipEntry(name));
    this.charset = charset;
    return this;
  }

  @Override
  public ArchiveBuilder addContent(Reader content)
      throws IOException {
    assertEntryOpenState(true);
    writeEntry(content, charset);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ArchiveBuilder endEntry() throws IOException {
    assertEntryOpenState(true);
    zos.closeEntry();
    charset = null;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] build() throws IOException {
    assertEntryOpenState(false);
    zos.close();
    return bos.toByteArray();
  }

  private void writeEntry(Reader reader, String charset) throws IOException {
    Writer writer = new OutputStreamWriter(zos, charset);
    char[] buf = new char[8192];
    int numRead = reader.read(buf);
    while (numRead != -1) {
      writer.write(buf, 0, numRead);
      numRead = reader.read(buf);
    }
    writer.flush();
  }

  private void assertEntryOpenState(boolean state) {
    if (state != (charset != null)) {
      throw new IllegalStateException("illegal entry state");
    }
  }

}
