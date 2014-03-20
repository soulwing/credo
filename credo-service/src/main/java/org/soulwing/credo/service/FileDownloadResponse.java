/*
 * File created on Mar 20, 2014 
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
import java.io.Writer;

/**
 * An object that represents a response to a client request to download a
 * file.
 *
 * @author Carl Harris
 */
public interface FileDownloadResponse {

  /**
   * Sets the MIME content type that will be indicated in the response.
   * @param contentType MIME content type
   */
  void setContentType(String contentType);
  
  /**
   * Sets the character encoding that will be indicated for a response 
   * containing a text content type.
   * @param encoding character set name
   */
  void setCharacterEncoding(String encoding);
  
  /**
   * Sets the name of the downloaded file that will be indicated in the 
   * response. 
   * @param fileName file name
   */
  void setFileName(String fileName);
  
  /**
   * Gets a writer that can be used to write the response body.
   * <p>
   * This method must not be called after a call to {@link #getOutputStream()}
   * @return writer
   * @throws IOException
   */
  Writer getWriter() throws IOException;
  
  /**
   * Gets an output stream that can be used to write the response body.
   * <p>
   * This method must not be called after a call to {@link #getWriter()}
   * @return output stream
   * @throws IOException
   */
  OutputStream getOutputStream() throws IOException;
  
}
