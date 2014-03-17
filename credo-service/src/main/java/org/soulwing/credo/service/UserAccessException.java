/*
 * File created on Mar 5, 2014 
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

/**
 * An exception thrown to indicate that access to a user profile was not
 * allowed (e.g. due to an incorrect password).
 *
 * @author Carl Harris
 */
public class UserAccessException extends Exception {

  private static final long serialVersionUID = 7632907461236227660L;

  /**
   * Constructs a new instance.
   * @param cause
   */
  public UserAccessException(Throwable cause) {
    super(cause);
  }
  
}
