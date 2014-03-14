/*
 * File created on Mar 2, 2014 
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
package org.soulwing.credo.repository;

import java.util.List;

import org.soulwing.credo.UserProfile;

/**
 * A repository of peristent {@link UserProfile} objects.
 *
 * @author Carl Harris
 */
public interface UserProfileRepository {

  /**
   * Adds the given user profile persistent to the repository.
   * @param profile the profile to add
   */
  void add(UserProfile profile);
  
  /**
   * Finds all existing user profiles.
   * @return list of user profiles
   */
  List<UserProfile> findAll();
  
  /**
   * Finds a user profile by it's unique identifier.
   * @param id ID of the profile to find
   * @return matching user profile or {@code null} if no such profile exists
   */
  UserProfile findById(Long id);
  
  /**
   * Finds the user profile with the given login name, if it exists.
   * @param loginName the subject login name
   * @return matching user profile or {@code null} if no such profile exists
   */
  UserProfile findByLoginName(String loginName);
  
}
