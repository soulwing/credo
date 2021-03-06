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

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.UserProfileWrapper;

/**
 * A concrete {@link GroupService} implementation.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GroupServiceBean implements GroupService {

  @Inject
  protected CredentialRepository credentialRepository;

  @Inject
  protected CredentialRequestRepository requestRepository;
  
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
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public Collection<GroupDetail> findAllGroups() {
    Set<GroupDetail> allDetails = new HashSet<>();
    
    Collection<UserGroupMember> members = 
        memberRepository.findByLoginName(userContextService.getLoginName());
    
    Set<GroupDetail> details = assembleGroupDetails(members);
    allDetails.addAll(details);
    
    for (GroupDetail detail : details) {
      UserGroupWrapper wrapper = (UserGroupWrapper) detail;
      UserGroup group = wrapper.getDelegate();
      List<UserGroup> descendants = groupRepository.findDescendants(group);
      wrapper.setInUse(wrapper.isInUse() || !descendants.isEmpty());
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
    return !credentialRepository.findAllByOwnerId(group.getId()).isEmpty()
        || !requestRepository.findAllByOwnerId(group.getId()).isEmpty();
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
