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

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.UserDetail;
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

  @Inject
  protected UserGroupRepository groupRepository; 

  @Inject
  protected UserProfileService profileService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigurableGroupEditor newEditor() {
    return configure(newGroupEditor.get(), groupRepository.newGroup(""),
        new ArrayList<UserDetail>());
  }

  private ConfigurableGroupEditor configure(ConfigurableGroupEditor editor,
      UserGroup group, Collection<UserDetail> members) {
    Long ownerId = profileService.getLoggedInUserProfile().getId();
    editor.setGroup(group);
    editor.setOwner(ownerId);
    editor.setMembers(members);
    editor.setUsers(profileService.findAllProfiles());
    editor.setMembership(new Long[] { ownerId });
    return editor;
  }
  
}
