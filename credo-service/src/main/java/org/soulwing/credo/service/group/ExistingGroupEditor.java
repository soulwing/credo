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
import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * A {@link ConfigurableGroupEditor} for an existing group.
 *
 * @author Carl Harris
 */
@ExistingGroup
@Dependent
public class ExistingGroupEditor extends AbstractGroupEditor {

  private static final long serialVersionUID = 7325864887394597055L;

  @Inject
  protected UserGroupMemberRepository memberRepository;

  private UserGroup group;

  private final Collection<Long> membersBefore = new ArrayList<>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void setGroup(UserGroup group) {
    this.group = group;
    if (group != null) {
      UserGroup owner = group.getOwner();
      if (owner != null) {
        setOwner(owner.getName());
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return group.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return group.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    group.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return group.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(String description) {
    group.setDescription(description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SecretKeyWrapper createSecretKey(UserGroup group, Errors errors) 
      throws PassphraseException, GroupAccessException {
    if (getPassword() == null) {
      throw new PassphraseException();
    }
    try {
      return protectionService.unprotect(group, getPassword());
    }
    catch (UserAccessException ex) {
      errors.addError("password", "passwordIncorrect");
      throw new PassphraseException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isNewMember(Long userId) {
    return !membersBefore.contains(userId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void beforeSave(Errors errors) {
    Collection<UserGroupMember> members = 
        memberRepository.findAllMembers(getName());
    for (UserGroupMember member : members) {
      membersBefore.add(member.getUser().getId());
    }    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UserGroup saveGroup(Errors errors) 
      throws MergeConflictException, GroupAccessException {
    try {
      return groupRepository.update(group);
    }
    catch (OwnerAccessControlException ex) {
      throw new GroupAccessException(ex.getGroupName());
    }
    catch (OptimisticLockException ex) {
      errors.addWarning("groupMergeConflict");
      throw new MergeConflictException();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void afterSave(Errors errors) {
    membersBefore.removeAll(Arrays.asList(getMembership()));
    for (Long userId : membersBefore) {
      UserGroupMember member = memberRepository.findByGroupAndProfileId(
          getName(), userId);
      if (member != null) {
        memberRepository.remove(member);
      }
    }
  }

}
