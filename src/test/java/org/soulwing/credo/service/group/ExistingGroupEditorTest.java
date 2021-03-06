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

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.util.Collections;

import javax.persistence.OptimisticLockException;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.junit.Test;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserAccessException;

/**
 * Unit tests for {@link ExistingGroupEditor}.
 *
 * @author Carl Harris
 */
public class ExistingGroupEditorTest
    extends AbstractGroupEditorTest<ExistingGroupEditor> {

  private static final Long PROFILE_ID = -100L;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private UserGroupMember member;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected ExistingGroupEditor newEditor() {
    return new ExistingGroupEditor();
  }

  @Override
  protected void onSetUp(ExistingGroupEditor editor) throws Exception {
    context.checking(new Expectations() { { 
      allowing(group).getOwner();
      will(returnValue(null));
    } });
    editor.setGroup(group);
    editor.setPassword(PASSWORD);
    editor.memberRepository = memberRepository;
  }

  @Test(expected = PassphraseException.class)
  public void testSaveWhenPassphaseException() throws Exception {
    Long[] membership = new Long[] { 1L, 2L, 3L };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations(returnValue(group)));
    context.checking(secretKeyExpectations(
        throwException(new UserAccessException(null))));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with("password"), with(containsString("Incorrect")),
          with(emptyArray()));
    } });
    editor.setUserId(1L);
    editor.setMembership(membership);
    editor.save(errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testSaveWhenGroupAccessDenied() throws Exception {
    Long ownerId = 1L;
    Long userId = 2L;
    Long[] membership = new Long[] { ownerId, userId };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations(throwException(
        new OwnerAccessControlException(GROUP_NAME, LOGIN_NAME))));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with(containsString("AccessDenied")),
          (Object[]) with(arrayContaining(GROUP_NAME)));
    } });
    editor.setUserId(ownerId);
    editor.setMembership(membership);
    editor.save(errors);
  }

  @Test(expected = MergeConflictException.class)
  public void testSaveWhenMergeConflict() throws Exception {
    Long ownerId = 1L;
    Long userId = 2L;
    Long[] membership = new Long[] { ownerId, userId };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations(throwException(
        new OptimisticLockException())));
    context.checking(new Expectations() { { 
      oneOf(errors).addWarning(with(containsString("MergeConflict")),
          with(emptyArray()));
    } });
    editor.setUserId(ownerId);
    editor.setMembership(membership);
    editor.save(errors);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Expectations beforeSaveExpectations(final Long[] membership) 
      throws Exception {    
    return new Expectations() { { 
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(memberRepository).findAllMembers(with(GROUP_NAME));
      will(returnValue(Collections.singleton(member)));
      oneOf(member).getUser();
      will(returnValue(profile));
      oneOf(profile).getId();
      will(returnValue(PROFILE_ID));
    } };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Expectations afterSaveExpectations(final Long[] membership) 
      throws Exception {
    return new Expectations() { { 
      oneOf(memberRepository).findByGroupAndProfileId(with(GROUP_NAME), 
          with(PROFILE_ID));
      will(returnValue(member));
      oneOf(memberRepository).remove(member);
    } };
  }

  @Override
  protected Expectations groupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(groupRepository).update(group);
      will(outcome);
    } };
  }

  @Override
  protected Expectations secretKeyExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(protectionService).unprotect(group, PASSWORD);
      will(outcome);
    } };
  }

  @Override
  protected Expectations protectionExpectations(final int memberCount) 
      throws Exception {
    return new Expectations() { {
      between(1, memberCount).of(protectionService).protect(group, secretKey, 
          profile);
    } };
  }

}
