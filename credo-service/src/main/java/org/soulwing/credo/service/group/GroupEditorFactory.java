/*
 * File created on Mar 14, 2014 
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


/**
 * A factory that produces {@link GroupEditor} objects.
 *
 * @author Carl Harris
 */
public interface GroupEditorFactory {

  /**
   * Creates an editor for a new group.
   * @return group editor
   */
  ConfigurableGroupEditor newEditor();
  
  /**
   * Creates an editor for an existing group.
   * @param groupId unique identifier of the group to edit
   * @return group editor
   * @throws NoSuchGroupException if there exists no group with the given
   *    identifier
   */
  ConfigurableGroupEditor newEditor(Long groupId)
      throws NoSuchGroupException;
  
}
