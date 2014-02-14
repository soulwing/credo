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

import java.io.Serializable;

/**
 * An object that keeps track of validation errors.
 *
 * @author Carl Harris
 */
public interface Errors extends Serializable {

  /**
   * Tests whether the receiver contains any errors.
   * @return {@code true} if the receiver contains at least one error
   */
  boolean hasErrors();
  
  /**
   * Tests whether the the receiver contains any warnings.
   * @return {@code true} if the receiver contains at least one warning
   */
  boolean hasWarnings();

  /**
   * Adds an error message to the receiver.
   * @param message the message to add
   * @param args arguments for the message
   */
  void addError(String message, Object... args);
  
  /**
   * Adds a warning message to the receiver.
   * @param message the message to add
   * @param args arguments for the message
   */
  void addWarning(String message, Object... args);
  
}
