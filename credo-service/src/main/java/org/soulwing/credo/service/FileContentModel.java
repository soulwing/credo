/*
 * File created on Feb 13, 2014 
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
import java.io.InputStream;

/**
 * A model that describes the contents of a file-like entity. 
 *
 * @author Carl Harris
 */
public interface FileContentModel {

  /**
   * Gets the name of the file.
   * @return file name
   */
  String getName();
  
  /**
   * Gets the content type of the receiver.
   * @return MIME content type
   */
  String getContentType();
  
  /**
   * Gets an input stream that can be used to retrieve the receiver's content.
   * @return input stream
   * @throws IOException
   */
  InputStream getInputStream() throws IOException;
  
}
