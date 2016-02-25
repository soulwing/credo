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

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserContextService;

/**
 * A {@link RemoveGroupService} implemented as an EJB.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RemoveGroupServiceBean implements RemoveGroupService {

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
  public GroupDetail findGroup(Long id) throws NoSuchGroupException {
    UserGroup group = groupRepository.findById(id);
    if (group == null) {
      throw new NoSuchGroupException();
    }
    return new UserGroupWrapper(group);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeGroup(Long id, Errors errors) 
      throws GroupAccessException, GroupException {

    UserGroup group = groupRepository.findById(id);
    if (group == null) return;
    
    UserGroupMember member = memberRepository.findByGroupAndLoginName(group, 
        userContextService.getLoginName());
    
    if (member == null) {
      String groupName = group.getName();
      errors.addError("groupAccessDenied", new Object[] { groupName });
      throw new GroupAccessException(groupName);
    }
    
    try {
      groupRepository.remove(group);
    }
    catch (PersistenceException ex) {
      errors.addError("groupInUse", new Object[] { id });
      throw new GroupException();
    }
  }
  

}
