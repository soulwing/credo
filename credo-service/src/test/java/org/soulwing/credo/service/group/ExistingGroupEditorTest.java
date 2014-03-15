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

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

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
  private SecretKeyWrapper secretKey;
  
  @Mock
  private UserGroupMember member;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected ExistingGroupEditor newEditor() {
    return new ExistingGroupEditor();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onSetUp(ExistingGroupEditor editor) throws Exception {
    editor.setPassword(PASSWORD);
    editor.memberRepository = memberRepository;
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected Expectations protectionExpectations(final int memberCount) 
      throws Exception {
    return new Expectations() { {
      oneOf(protectionService).unprotect(group, PASSWORD);
      will(returnValue(secretKey));
      between(1, memberCount).of(protectionService).protect(group, secretKey, 
          profile);
    } };
  }

}
