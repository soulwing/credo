/*
 * File created on Feb 24, 2014 
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
package org.soulwing.credo.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An object that represents the result of acting upon a request to
 * export a credential.
 *
 * @author Carl Harris
 */
public interface ExportPreparation {

  /**
   * Gets the assigned file name.
   * @return file name
   */
  String getFileName();
  
  /**
   * Gets the MIME content type descriptor.
   * @return content type descriptor
   */
  String getContentType();
  
  /**
   * Gets the character encoding for text content types.
   * @return character encoding or {@code null} if content type is not a text
   *    subtype
   */
  String getCharacterEncoding();
  
  /**
   * Writes the exported content to the given output stream.
   * @param outputStream the target output stream
   * @throws IOExceptions
   */
  void writeContent(OutputStream outputStream) throws IOException;
  
}
