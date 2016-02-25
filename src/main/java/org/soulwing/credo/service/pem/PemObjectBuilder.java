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

/**
 * A builder for a PEM object.
 *
 * @author Carl Harris
 */
public interface PemObjectBuilder {

  /**
   * Sets the receiver's PEM object type designator.
   * @param type type designator
   * @return the receiver
   */
  PemObjectBuilder setType(String type);
  
  /**
   * Sets a header value on the receiver.
   * @param name name of the header
   * @param value value for the header (which will be coerced to a string 
   *    using {@link Object#toString()})
   * @return the receiver
   */
  PemObjectBuilder setHeader(String name, Object value);
  
  /**
   * Appends data bytes to the end of the receiver's buffer.
   * @param data an array of data bytes to append
   * @return the receiver
   */
  PemObjectBuilder append(byte[] data);
  
  /**
   * Builds the PEM object.
   * @return 
   */
  PemObjectWrapper build();
  
}
