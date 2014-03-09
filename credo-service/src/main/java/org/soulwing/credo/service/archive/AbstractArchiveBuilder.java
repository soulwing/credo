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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

/**
 * An abstract base for {@link ArchiveBuilder} implementations.
 *
 * @author Carl Harris
 */
abstract class AbstractArchiveBuilder implements ArchiveBuilder {

  protected final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  
  private ByteArrayOutputStream content;
  
  private String charset;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public final ArchiveBuilder beginEntry(String name, String charset)
      throws IOException {
    assertEntryInProgress(false);
    this.charset = charset;
    this.content = new ByteArrayOutputStream();
    onBeginEntry(name, charset);
    return this;
  }

  protected abstract void onBeginEntry(String name, String charset)
      throws IOException;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public final ArchiveBuilder addContent(Reader content)
      throws IOException {
    assertEntryInProgress(true);
    writeContent(content, charset);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ArchiveBuilder addContent(String content) throws IOException {
    return addContent(new StringReader(content));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ArchiveBuilder endEntry() throws IOException {
    assertEntryInProgress(true);
    content.flush();
    content.close();
    byte[] buf = content.toByteArray();
    beforeWriteEntry(buf);
    getOutputStream().write(buf);
    onEndEntry();
    charset = null;
    return this;
  }

  protected void beforeWriteEntry(byte[] content) throws IOException {    
  }
  
  protected abstract void onEndEntry() throws IOException;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] build() throws IOException {
    assertEntryInProgress(false);
    onBuild();
    return buffer.toByteArray();
  }

  protected abstract void onBuild() throws IOException;
  
  protected abstract OutputStream getOutputStream();
  
  private void writeContent(Reader reader, String charset) throws IOException {
    Writer writer = new OutputStreamWriter(content, charset);
    char[] buf = new char[8192];
    int numRead = reader.read(buf);
    while (numRead != -1) {
      writer.write(buf, 0, numRead);
      numRead = reader.read(buf);
    }
    writer.flush();
  }

  private void assertEntryInProgress(boolean state) {
    if (state != (charset != null)) {
      throw new IllegalStateException("illegal entry state");
    }
  }


}
