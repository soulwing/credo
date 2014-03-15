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
import static org.jmock.Expectations.onConsecutiveCalls;
import static org.jmock.Expectations.returnValue;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.protect.GroupProtectionService;

/**
 * An abstract base for unit tests of a {@link AbstractGroupEditor}
 * subclass.
 *
 * @author Carl Harris
 */
public abstract class AbstractGroupEditorTest<T extends AbstractGroupEditor> {

  protected static final String FULL_NAME = "Full Name";

  protected static final String LOGIN_NAME = "loginName";

  protected static final String GROUP_NAME = "groupName";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private UserProfileRepository profileRepository;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  protected UserGroup group;
  
  @Mock
  protected UserProfile profile;
  
  @Mock
  protected GroupProtectionService protectionService;
  
  @Mock
  private UserDetail user;
  
  @Mock
  private Errors errors;

  protected T editor;
  
  @Before
  public final void setUp() throws Exception {
    editor = newEditor();
    editor.setGroup(group);
    editor.setUsers(Collections.singleton(user));
    editor.protectionService = protectionService;
    editor.profileRepository = profileRepository;
    editor.groupRepository = groupRepository;
    onSetUp(editor);
  }
  
  protected abstract T newEditor();
  
  protected void onSetUp(T editor) throws Exception {    
  }
  
  @Test
  public void testSave() throws Exception {
    Long[] membership = new Long[] { 1L, 2L, 3L };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations());
    context.checking(profileExpectations(membership.length,
        returnValue(profile)));
    context.checking(protectionExpectations(membership.length));
    context.checking(errorCheckExpectations(returnValue(false)));
    context.checking(afterSaveExpectations(membership));
    
    editor.setOwner(1L);
    editor.setMembership(membership);
    editor.save(errors);
  }

  @Test(expected = GroupEditException.class)
  public void testSaveWhenUserNotFound() throws Exception {
    Long ownerId = 1L;
    Long userId = 2L;
    Long[] membership = new Long[] { ownerId, userId };
    context.checking(beforeSaveExpectations(membership));
    context.checking(groupExpectations());
    context.checking(profileExpectations(membership.length,
        onConsecutiveCalls(returnValue(profile), returnValue(null))));
    context.checking(protectionExpectations(membership.length));
    context.checking(errorExpectations(userId, membership.length));
    context.checking(errorCheckExpectations(returnValue(true)));
    editor.setOwner(ownerId);
    editor.setMembership(membership);
    editor.setUsers(Collections.singleton(user));
    editor.save(errors);
  }

  @Test(expected = GroupEditException.class)
  public void testSaveWhenUserNotMember() throws Exception {
    Long[] membership = new Long[] { 1L }; 
    context.checking(beforeSaveExpectations(membership));
    context.checking(new Expectations() { { 
      allowing(errors).addWarning(with("members"),
          with(containsString("MustBeMember")), 
          with(emptyArray()));
    } });
    
    context.checking(groupExpectations());
    context.checking(profileExpectations(membership.length + 1,
        returnValue(profile)));
    context.checking(protectionExpectations(membership.length + 1));
    context.checking(errorCheckExpectations(returnValue(true)));
    
    editor.setOwner(-1L);
    editor.setMembership(membership);
    editor.save(errors);
  }

  protected Expectations beforeSaveExpectations(Long[] membership) 
      throws Exception {
    return new Expectations();
  }

  protected Expectations afterSaveExpectations(Long[] membership) 
      throws Exception {
    return new Expectations();
  }

  private Expectations profileExpectations(final int memberCount,
      final Action outcome) {
    return new Expectations() { { 
      between(1, memberCount).of(profileRepository).findById(
          with(any(Long.class)));
      will(outcome);
    } };
  }
  
  protected abstract Expectations protectionExpectations(int memberCount)
      throws Exception;
  
  private Expectations groupExpectations() {
    return new Expectations() { { 
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(groupRepository).add(group);
    } };
  }

  private Expectations errorCheckExpectations(final Action outcome) { 
    return new Expectations() { { 
      allowing(errors).hasErrors();
      will(returnValue(false));
      oneOf(errors).hasWarnings();
      will(outcome);
    } };
  }
  
  private Expectations errorExpectations(final Long id, 
      final int memberCount) {
    return new Expectations() { {
      allowing(user).getId();
      will(returnValue(id));
      allowing(user).getLoginName();
      will(returnValue(LOGIN_NAME));
      allowing(user).getFullName();
      will(returnValue(FULL_NAME));
      between(1,  memberCount).of(errors).addWarning(
          with("members"), with(containsString("NoSuchUser")), 
          (Object[]) with(arrayContaining(LOGIN_NAME, FULL_NAME)));
    } };
  }

}
