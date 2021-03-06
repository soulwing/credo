/*
 * File created on Feb 19, 2014 
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
package org.soulwing.credo.service.crypto;

import java.io.IOException;

import javax.security.auth.x500.X500Principal;

/**
 * A wrapper for a PKCS#10 certification request.
 *
 * @author Carl Harris
 */
public interface CertificationRequestWrapper {

  /**
   * Gets the subject name of this certificate.
   * @return subject name
   */
  X500Principal getSubject();
  
  /**
   * Gets the content of this certification request in a suitable string 
   * encoding (typically PEM).
   * @return certification request content
   * @throws IOException
   */
  String getContent() throws IOException;
  
}
