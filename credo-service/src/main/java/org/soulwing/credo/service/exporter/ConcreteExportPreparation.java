/*
 * File created on Feb 25, 2014 
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

import java.io.IOException;
import java.io.OutputStream;

import org.soulwing.credo.service.ExportPreparation;

/**
 * A concrete {@link ExportPreparation} implementation.
 *
 * @author Carl Harris
 */
public class ConcreteExportPreparation implements ExportPreparation {

  private final String fileName;
  private final String contentType;
  private final String characterEncoding;
  private final byte[] content;
  
  /**
   * Constructs a new instance.
   * @param fileName
   * @param contentType
   * @param content
   */
  public ConcreteExportPreparation(String fileName, String contentType,
      byte[] content) {
    this(fileName, contentType, null, content);
  }
  
  /**
   * Constructs a new instance.
   * @param fileName
   * @param contentType
   * @param characterEncoding
   * @param content
   */
  public ConcreteExportPreparation(String fileName, String contentType,
      String characterEncoding, byte[] content) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.characterEncoding = characterEncoding;
    this.content = content;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getFileName() {
    return fileName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContentType() {
    return contentType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCharacterEncoding() {
    return characterEncoding;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeContent(OutputStream outputStream) throws IOException {
    outputStream.write(content);
  }

}
