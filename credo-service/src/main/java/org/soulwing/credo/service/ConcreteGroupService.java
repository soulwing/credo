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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import org.soulwing.credo.repository.CredentialRepository;
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
  protected CredentialRepository credentialRepository;

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

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public GroupDetail findGroup(Long id) throws NoSuchGroupException {
    String loginName = userContextService.getLoginName();
    Collection<UserGroupMember> members = memberRepository
        .findByGroupIdAndLoginName(id, loginName);
    if (members.isEmpty()) {
      throw new NoSuchGroupException();
    }
    return assembleGroupDetails(members).iterator().next();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public Collection<GroupDetail> findAllGroups() {
    Set<GroupDetail> allDetails = new HashSet<>();
    
    Collection<UserGroupMember> members = 
        memberRepository.findByLoginName(userContextService.getLoginName());
    
    Set<GroupDetail> details = assembleGroupDetails(members);
    allDetails.addAll(details);
    
    for (GroupDetail detail : details) {
      UserGroup group = ((UserGroupWrapper) detail).getDelegate();
      List<UserGroup> descendants = groupRepository.findDescendants(group);
      allDetails.addAll(assembleGroupDetails(descendants));
    }
    
    return sortedDetails(allDetails);
  }

  private Set<GroupDetail> assembleGroupDetails(
      Collection<UserGroupMember> members) {
    Set<GroupDetail> details = new HashSet<>();
    UserGroupWrapper wrapper = null;
    for (UserGroupMember member : members) {
      UserGroup group = member.getGroup();
      String groupName = group.getName();
      if (UserGroup.SELF_GROUP_NAME.equals(groupName)) continue;
      if (wrapper == null || !wrapper.getName().equals(groupName)) {
        wrapper = new UserGroupWrapper(group);
        wrapper.setInUse(resolveGroupInUse(group));
        details.add(wrapper);
      }
      UserProfile user = member.getUser();
      if (user != null) {
        wrapper.addMember(new UserProfileWrapper(user));
      }
    }
    return details;
  }

  private Set<GroupDetail> assembleGroupDetails(List<UserGroup> groups) {
    Set<GroupDetail> details = new HashSet<>();  
    for (UserGroup group : groups) {
      UserGroupWrapper wrapper = new UserGroupWrapper(group);
      wrapper.setInUse(resolveGroupInUse(group));
      for (UserGroupMember member : group.getMembers()) {
        wrapper.addMember(new UserProfileWrapper(member.getUser()));
      }
      details.add(wrapper);
    }
    return details;
  }
  
  private List<GroupDetail> sortedDetails(Set<GroupDetail> details) {
    List<GroupDetail> allDetails = new ArrayList<>(details.size());
    allDetails.addAll(details);
    Collections.sort(allDetails, new Comparator<GroupDetail>() {
      @Override
      public int compare(GroupDetail a, GroupDetail b) {
        return a.getName().compareTo(b.getName());
      }
    });
    return allDetails;
  }
  
  private boolean resolveGroupInUse(UserGroup group) {
    return !credentialRepository.findAllByOwnerId(group.getId()).isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public GroupEditor editGroup(Long id) throws NoSuchGroupException {
    return editorFactory.newEditor(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveGroup(GroupEditor editor, Errors errors)
      throws GroupEditException, NoSuchGroupException, GroupAccessException,
          PassphraseException, MergeConflictException {
    Validate.isTrue(editor instanceof ConfigurableGroupEditor);
    ((ConfigurableGroupEditor) editor).save(errors);
  }

  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void removeGroup(Long id, Errors errors) throws GroupEditException,
      NoSuchGroupException {
    String loginName = userContextService.getLoginName();
    Collection<UserGroupMember> members = memberRepository
        .findByGroupIdAndLoginName(id, loginName);
    if (members.isEmpty()) {
      errors.addError("groupNotFound", id);
      throw new NoSuchGroupException();
    }
    if (!credentialRepository.findAllByOwnerId(id).isEmpty()) {
      errors.addError("groupInUse", id);
      throw new GroupEditException();
    }
    
    for (UserGroupMember member : members) {
      memberRepository.remove(member);
    }
    
    groupRepository.remove(id);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExistingGroup(String groupName)
      throws GroupAccessException {
    String loginName = userContextService.getLoginName();
    UserGroup group = groupRepository.findByGroupName(groupName, loginName);
    boolean exists = group != null;
    if (exists) {
      UserGroupMember member = memberRepository.findByGroupAndLoginName(
          group, loginName);
      if (member == null) {
        throw new GroupAccessException(groupName);
      }
    }
    return exists;
  }
 
}
