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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserContextService;

/**
 * A {@link GroupResolver} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class GroupResolverBean implements GroupResolver {

  @Inject
  protected CreateGroupService groupService;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup resolveGroup(String groupName, Errors errors) 
      throws GroupAccessException, NoSuchGroupException {
    UserGroup group = null;
    try {
      group = findGroup(groupName);
    }
    catch (NoSuchGroupException ex) {
      GroupEditor editor = groupService.newGroup();
      editor.setName(groupName);
      try {
        groupService.saveGroup(editor, errors);
        group = findGroup(groupName);
      }
      catch (EditException|PassphraseException|MergeConflictException oex) {
        throw new RuntimeException(oex);
      }
    }
    return group;
  }

  private UserGroup findGroup(String groupName)
      throws NoSuchGroupException {
    UserGroup group = groupRepository.findByGroupName(
        groupName, userContextService.getLoginName());
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return group;
  }
  
}
