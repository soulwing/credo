/*
 * File created on Feb 28, 2014 
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
package org.soulwing.credo.service.pem;

import java.io.IOException;
import java.io.Writer;


/**
 * A wrapper for a PEM header.
 *
 * @author Carl Harris
 */
public interface PemObjectWrapper {

  /**
   * Gets a PEM header.
   * @param name name of the header
   * @return header or {@code null} if no header exists with the given name
   */
  PemHeaderWrapper getHeader(String name);
  
  /**
   * Gets the length of the content in bytes.
   * @return length
   */
  int getContentLength();
  
  /**
   * Gets the content.
   * @return byte array containing the PEM object contents
   */
  byte[] getContent();
  
  /**
   * Copies content bytes from the PEM object.
   * @param start
   * @param end
   * @return an array containing the requested range of bytes from the 
   *    PEM content
   */
  byte[] copyBytes(int start, int end);
  
  /**
   * Gets the PEM encoded representation of the data in this wrapper. 
   * @return PEM encoded data
   */
  String getEncoded();
  
  /**
   * Writes the PEM object represented by this wrapper to the given writer.
   * @param writer the target writer
   * @throws IOException
   */
  void writeContent(Writer writer) throws IOException;
 
}
