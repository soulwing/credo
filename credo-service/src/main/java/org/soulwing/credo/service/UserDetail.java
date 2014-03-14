/*
 * File created on Mar 10, 2014 
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
 * An object that describes the details of a user profile.
 *
 * @author Carl Harris
 */
public interface UserDetail {

  /**
   * Gets the profile's unique identifier.
   * @return unique identifier
   */
  Long getId();
  
  /**
   * Gets the user's login name.
   * @return login name
   */
  String getLoginName();
  
  /**
   * Gets the user's full name.
   * @return full name
   */
  String getFullName();
  
}
