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
import java.io.Reader;

/**
 * A builder for a archive resource (e.g. a ZIP archive).
 *
 * @author Carl Harris
 */
public interface ArchiveBuilder {

  /**
   * Begins a new entry in the receiver.
   * @param name name for the entry
   * @param charset character encoding for the entry's content
   * @return the recevier
   * @throws IOException
   */
  ArchiveBuilder beginEntry(String name, String charset) throws IOException;
  
  /**
   * Adds content to the current entry of the receiver.
   * @param content the content to add
   * @return the receiver
   * @throws IOException
   */
  ArchiveBuilder addContent(Reader content) throws IOException;
  
  /**
   * Ends the current entry in the receiver.
   * @return the receiver.
   * @throws IOException
   */
  ArchiveBuilder endEntry() throws IOException;
  
  /**
   * Builds the archive represented by the receiver's configuration.
   * @return byte array containing archive content
   */ 
  byte[] build() throws IOException;
  
}
