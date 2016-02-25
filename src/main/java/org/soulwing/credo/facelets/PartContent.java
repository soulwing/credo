/*
 * File created on Feb 14, 2014 
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
package org.soulwing.credo.facelets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.http.Part;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.FileContentModel;

/**
 * A {@link FileContentModel} that wraps a {@link Part}.
 *
 * @author Carl Harris
 */
public class PartContent implements FileContentModel, Serializable {
  
  private static final long serialVersionUID = -8172871818481456381L;

  private byte[] content;
  
  private transient Part part;
  
  
  /**
   * Gets the {@link Part} delegate.
   * @return part delegate
   */
  public Part getPart() {
    return part;
  }

  /**
   * Sets the {@link Part} delegate.
   * @param part the part delegate to set
   */
  public void setPart(Part part) {
    this.part = part;
    this.content = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    if (part == null) return null;
    return part.getSubmittedFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getInputStream() throws IOException {
    Validate.notNull(content, "content not loaded");
    return new ByteArrayInputStream(content);
  }

  /**
   * Tests whether the receiver has loadable content.
   * @return {@code true} if the receiver has content to load
   */
  public boolean isLoadable() {
    return part != null;
  }
  
  /**
   * Loads the uploaded content.
   * @throws IOException
   */
  public void load() throws IOException {
    if (content != null) return;
    Validate.notNull(part, "part not set");
    try (InputStream inputStream = part.getInputStream(); 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      byte[] buf = new byte[8192];
      int numRead = inputStream.read(buf);
      while (numRead != -1) {
        outputStream.write(buf, 0, numRead);
        numRead = inputStream.read(buf);
      }
      outputStream.flush();
      this.content = outputStream.toByteArray();
    }
  }
  
}
