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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.jmock.Expectations.throwException;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.junit.Test;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
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
  private static final Password PASSWORD = new Password(new char[0]);
  
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
    editor.setPassword(PASSWORD);
    editor.memberRepository = memberRepository;
  }

  @Test(expected = PassphraseException.class)
  public void testSaveWhenPassphaseException() throws Exception {
    Long[] membership = new Long[] { 1L, 2L, 3L };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations());
    context.checking(secretKeyExpectations(
        throwException(new UserAccessException(null))));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with("password"), with(containsString("Incorrect")),
          with(emptyArray()));
    } });
    editor.setOwner(1L);
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
  protected Expectations groupExpectations() throws Exception {
    return new Expectations() { { 
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(groupRepository).update(group);
      will(returnValue(group));
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
