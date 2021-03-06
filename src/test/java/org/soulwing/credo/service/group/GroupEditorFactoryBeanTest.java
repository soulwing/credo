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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.UserProfileService;

/**
 * Unit tests for {@link GroupEditorFactoryBean}.
 *
 * @author Carl Harris
 */
public class GroupEditorFactoryBeanTest {

  private static final Long OWNER_ID = -1L;
  
  private static final Long USER_ID = -2L;
  
  private static final Long GROUP_ID = -1L;
  
  private static final String GROUP_NAME = "someGroup";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<ConfigurableGroupEditor> newGroupEditor;
  
  @Mock
  private Instance<ConfigurableGroupEditor> existingGroupEditor;
  
  @Mock
  private UserProfileService profileService;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private ConfigurableGroupEditor editor;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserProfile profile;
  
  @Mock
  private UserGroupMember member;
  
  @Mock
  private Collection<UserDetail> users;
  
  private GroupEditorFactoryBean editorFactory =
      new GroupEditorFactoryBean();
  
  @Before
  public void setUp() throws Exception {
    editorFactory.newGroupEditor = newGroupEditor;
    editorFactory.existingGroupEditor = existingGroupEditor;
    editorFactory.profileService = profileService;
    editorFactory.groupRepository = groupRepository;   
    editorFactory.memberRepository = memberRepository;
  }

  @Test
  public void testNewEditorForNewGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(newGroupEditor).get();
      will(returnValue(editor));
      oneOf(profileService).findAllProfiles();
      will(returnValue(users));
      oneOf(profileService).getLoggedInUserProfile();
      will(returnValue(profile));
      oneOf(profile).getId();
      will(returnValue(OWNER_ID));
      oneOf(editor).setUserId(with(OWNER_ID));
      oneOf(editor).setMembership(with(arrayContaining(OWNER_ID)));
      oneOf(editor).setUsers(with(same(users)));
    } });
    
    assertThat(editorFactory.newEditor(), is(sameInstance(editor)));
  }

  @Test
  public void testNewEditorForExistingGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(existingGroupEditor).get();
      will(returnValue(editor));
      oneOf(groupRepository).findById(with(GROUP_ID));
      will(returnValue(group));
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(memberRepository).findAllMembers(with(GROUP_NAME));
      will(returnValue(Collections.singleton(member)));
      oneOf(member).getUser();
      will(returnValue(profile));
      oneOf(profileService).findAllProfiles();
      will(returnValue(users));
      oneOf(profileService).getLoggedInUserProfile();
      will(returnValue(profile));
      exactly(2).of(profile).getId();
      will(onConsecutiveCalls(returnValue(USER_ID), returnValue(OWNER_ID)));
      oneOf(editor).setGroup(with(same(group)));
      oneOf(editor).setUserId(with(OWNER_ID));
      oneOf(editor).setMembership(with(arrayContaining(USER_ID)));
      oneOf(editor).setUsers(with(same(users)));
    } });
    
    assertThat(editorFactory.newEditor(GROUP_ID), is(sameInstance(editor)));
  }

}

