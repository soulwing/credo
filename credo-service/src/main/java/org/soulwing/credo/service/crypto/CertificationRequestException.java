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
package org.soulwing.credo.service.crypto;

/**
 * An exception thrown by a {@link CertificationRequestBuilder} to indicate
 * that there was a problem in generating the certification request.
 *
 * @author Carl Harris
 */
public class CertificationRequestException extends Exception {

  private static final long serialVersionUID = -773054569839805189L;

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public CertificationRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new instance.
   * @param message
   */
  public CertificationRequestException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param cause
   */
  public CertificationRequestException(Throwable cause) {
    super(cause);
  }

}
