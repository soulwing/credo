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

import java.util.Collection;

import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.protect.GroupAccessException;

/**
 * A {@link GroupEditor} with a save method.
 *
 * @author Carl Harris
 */
public interface ConfigurableGroupEditor extends GroupEditor {

  /**
   * Sets the group to edit.
   * @param group the group to set
   */
  void setGroup(UserGroup group);
  
  /**
   * Sets the ID of the group owner.
   * @param id the ID to set
   */
  void setOwner(Long id);
  
  /**
   * Sets the collection of users who are existing members of the group.
   * @param members the members to set
   */
  void setMembers(Collection<UserDetail> members);
  
  /**
   * Sets the collection of users who will be made available to set as 
   * group members.
   * @param users the users to set
   */
  void setUsers(Collection<UserDetail> users);
  
  /**
   * Gets the password to be used to gain access to the group's secret key.
   * @return password or {@code null} if none has been set
   */
  Password getPassword();
  
  /**
   * Sets the password to be used to gain access to the group's secret key.
   * @param password the password to set
   */
  void setPassword(Password password);
  
  /**
   * Applies the state of this editor, effectively making the edits it 
   * represents persistent.
   * @param errors an errors object that will be updated in the case of
   *    recoverable error(s)
   * @throws GroupEditException if a recoverable error occurs
   * @throws NoSuchGroupException if the group identified by the editor
   *    was removed after the editor was created
   * @throws PassphraseException if a passphrase is required and was not
   *    provided or is incorrect
   * @throws GroupAccessException if the logged in user is not a member
   *    of the edited group
   */
  void save(Errors errors) throws GroupEditException, NoSuchGroupException,
      PassphraseException, GroupAccessException;
 
}
