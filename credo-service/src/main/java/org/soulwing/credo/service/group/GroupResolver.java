/*
 * File created on Apr 14, 2014 
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
package org.soulwing.credo.service.group;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;

/**
 * A service that resolves a persistent group by locating in a repository,
 * creating an persisting it if necessary.
 *
 * @author Carl Harris
 */
public interface GroupResolver {

  /**
   * Resolves a group name.
   * <p>
   * If there exists no group entity with the given name, an entity with
   * that name is created with the logged in user as the only member.
   * 
   * @param groupName group name to resolve
   * @return persistent user group
   * @throws GroupAccessException if the logged in user is not authorized to
   *    access the specified group
   * @throws NoSuchGroupException if the group cannot be found after being
   *    created
   */
  UserGroup resolveGroup(String groupName, Errors errors) 
      throws GroupAccessException, NoSuchGroupException;
  
}
