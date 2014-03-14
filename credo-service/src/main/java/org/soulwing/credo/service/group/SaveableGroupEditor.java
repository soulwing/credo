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

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.NoSuchGroupException;

/**
 * A {@link GroupEditor} with a save method.
 *
 * @author Carl Harris
 */
public interface SaveableGroupEditor extends GroupEditor {

  /**
   * Applies the state of this editor, effectively making the edits it 
   * represents persistent.
   * @param errors an errors object that will be updated in the case of
   *    recoverable error(s)
   * @throws NoSuchGroupException if the group identified by the editor
   *    was removed after the editor was created
   * @throws GroupEditException if a recoverable error occurs
   */
  void save(Errors errors) throws NoSuchGroupException, GroupEditException;
  
}
