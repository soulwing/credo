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

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.group.ConfigurableGroupEditor;
import org.soulwing.credo.service.group.GroupEditorFactory;
import org.soulwing.credo.service.protect.GroupAccessException;

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
    return assembleGroupDetails(memberRepository.findByLoginName(
        userContextService.getLoginName()));
  }

  private Collection<GroupDetail> assembleGroupDetails(
      Collection<UserGroupMember> members) {
    Collection<GroupDetail> groupDetails = new ArrayList<>();
    UserGroupWrapper wrapper = null;
    for (UserGroupMember member : members) {
      UserGroup group = member.getGroup();
      String groupName = group.getName();
      if (UserGroup.SELF_GROUP_NAME.equals(groupName)) continue;
      if (wrapper == null || !wrapper.getName().equals(groupName)) {
        wrapper = new UserGroupWrapper(group);
        groupDetails.add(wrapper);
      }
      wrapper.addMember(new UserProfileWrapper(member.getUser()));
    }
    return groupDetails;
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
      throws GroupEditException, NoSuchGroupException, PassphraseException, 
          AccessDeniedException {
    try {
      Validate.isTrue(editor instanceof ConfigurableGroupEditor);
      ((ConfigurableGroupEditor) editor).save(errors);
    }
    catch (GroupAccessException ex) {
      throw new AccessDeniedException();
    }
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
  
}
