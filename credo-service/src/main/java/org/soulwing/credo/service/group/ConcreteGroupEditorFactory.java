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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.UserProfileService;

/**
 * A factory that produces {@link GroupEditor} objects.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteGroupEditorFactory implements GroupEditorFactory {

  @Inject @NewGroup
  protected Instance<ConfigurableGroupEditor> newGroupEditor;

  @Inject @ExistingGroup
  protected Instance<ConfigurableGroupEditor> existingGroupEditor;
  
  @Inject
  protected UserGroupRepository groupRepository; 

  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @Inject
  protected UserProfileService profileService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigurableGroupEditor newEditor() {
    return configure(newGroupEditor.get(), groupRepository.newGroup(""),
        new LinkedHashSet<Long>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigurableGroupEditor newEditor(Long groupId) 
      throws NoSuchGroupException {
    UserGroup group = findGroupById(groupId);
    Set<Long> members = findMembers(group);
    return configure(existingGroupEditor.get(), group, members);      
  }
  
  private UserGroup findGroupById(Long groupId) throws NoSuchGroupException {
    UserGroup group = groupRepository.findById(groupId);
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return group;
  }

  private Set<Long> findMembers(UserGroup group) {
    Set<Long> members = new LinkedHashSet<>();
    for (UserGroupMember member : 
        memberRepository.findAllMembers(group.getName())) { 
      members.add(member.getUser().getId());
    }
    return members;
  }

  private ConfigurableGroupEditor configure(ConfigurableGroupEditor editor,
      UserGroup group, Set<Long> membership) {
    Long ownerId = profileService.getLoggedInUserProfile().getId();
    membership.add(ownerId);
    editor.setGroup(group);
    editor.setOwner(ownerId);
    editor.setUsers(profileService.findAllProfiles());
    editor.setMembership(membership.toArray(new Long[membership.size()]));
    return editor;
  }
  
}
