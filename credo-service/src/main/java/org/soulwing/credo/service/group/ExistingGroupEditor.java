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

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.protect.GroupAccessException;
import org.soulwing.credo.service.protect.UserAccessException;

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

  private final Collection<Long> membersBefore = new ArrayList<>();
  
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
  protected UserGroup saveGroup(UserGroup group) {
    return groupRepository.update(group);
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
