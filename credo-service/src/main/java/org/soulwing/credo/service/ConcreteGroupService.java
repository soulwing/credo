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
package org.soulwing.credo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.group.ConfigurableGroupEditor;
import org.soulwing.credo.service.group.GroupEditorFactory;

/**
 * A concrete {@link GroupService} implementation.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteGroupService implements GroupService {

  @Inject
  protected GroupEditorFactory editorFactory;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public GroupEditor newGroup() {
    return editorFactory.newEditor();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<GroupDetail> findAllGroups() {
    Set<? extends UserGroup> groups = groupRepository.findByLoginName(
        userContextService.getLoginName());
    Collection<GroupDetail> groupDetails = new ArrayList<>();
    for (UserGroup group : groups) {
      UserGroupWrapper groupDetail = new UserGroupWrapper(group);
      groupDetails.add(groupDetail);
      Collection<UserGroupMember> members = 
          memberRepository.findAllMembers(group.getName());
      for (UserGroupMember member : members) {
        groupDetail.addMember(new UserProfileWrapper(member.getUser()));
      }
    }
    return groupDetails;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveGroup(GroupEditor editor, Errors errors)
      throws NoSuchGroupException, GroupEditException {
    Validate.isTrue(editor instanceof ConfigurableGroupEditor);
    ((ConfigurableGroupEditor) editor).save(errors);
  }
 
  
  private static class UserGroupWrapper implements GroupDetail {

    private final UserGroup delegate;
    private final Collection<UserDetail> members = new ArrayList<>();
    
    /**
     * Constructs a new instance.
     * @param delegate
     */
    public UserGroupWrapper(UserGroup delegate) {
      this.delegate = delegate;
    }

    public void addMember(UserDetail member) {
      this.members.add(member);
    }
    
    @Override
    public Long getId() {
      return delegate.getId();
    }

    @Override
    public String getName() {
      return delegate.getName();
    }

    @Override
    public String getDescription() {
      return delegate.getDescription();
    }

    @Override
    public Collection<UserDetail> getMembers() {
      return members;
    }
    
  }
  
  private static class UserProfileWrapper implements UserDetail {

    private UserProfile delegate;
    
    /**
     * Constructs a new instance.
     * @param delegate
     */
    public UserProfileWrapper(UserProfile delegate) {
      this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
      return delegate.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoginName() {
      return delegate.getLoginName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullName() {
      return delegate.getFullName();
    }
    
  }

}
