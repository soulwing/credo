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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.soulwing.credo.service.group.ConfigurableGroupEditor;
import org.soulwing.credo.service.group.GroupEditorFactory;

/**
 * Unit tests for {@link ConcreteGroupService}.
 *
 * @author Carl Harris
 */
public class ConcreteGroupServiceTest {

  private static final String GROUP_NAME = "groupName";

  private static final String LOGIN_NAME1 = "user1";
  
  private static final String LOGIN_NAME2 = "user2";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupEditorFactory editorFactory;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private ConfigurableGroupEditor editor;
  
  @Mock
  private Errors errors;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserProfile profile;
  
  private ConcreteGroupService service = new ConcreteGroupService();
  
  @Before
  public void setUp() throws Exception {
    service.editorFactory = editorFactory;
    service.memberRepository = memberRepository;
    service.userContextService = userContextService;    
  }
  
  @Test
  public void testNewGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editorFactory).newEditor();
      will(returnValue(editor));
    } });
    
    assertThat(service.newGroup(), is(sameInstance((GroupEditor) editor)));
  }
  
  @Test
  public void testFindAllGroups() throws Exception {
    final UserProfile profile1 = context.mock(UserProfile.class, "profile1");
    final UserProfile profile2 = context.mock(UserProfile.class, "profile2");
    final UserGroupMember member1 = context.mock(UserGroupMember.class, "member1");
    final UserGroupMember member2 = context.mock(UserGroupMember.class, "member2");
    final List<UserGroupMember> members = Arrays.asList(new UserGroupMember[] { 
        member1, member2
    });
   
    context.checking(new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(memberRepository).findByLoginName(with(LOGIN_NAME1));
      will(returnValue(members));
      oneOf(member1).getGroup();
      will(returnValue(group));
      oneOf(member2).getGroup();
      will(returnValue(group));
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(member1).getUser();
      will(returnValue(profile1));
      oneOf(profile1).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(member2).getUser();
      will(returnValue(profile2));
      oneOf(profile2).getLoginName();
      will(returnValue(LOGIN_NAME2));
    } });
    
    Iterator<GroupDetail> i = service.findAllGroups().iterator();
    assertThat(i.hasNext(), is(true));
    GroupDetail group = i.next();
    assertThat(group.getName(), is(equalTo(GROUP_NAME)));
    
    Iterator<UserDetail> j = group.getMembers().iterator();
    assertThat(j.hasNext(), is(true));
    UserDetail user1 = j.next();
    assertThat(user1.getLoginName(), is(equalTo(LOGIN_NAME1)));        
    assertThat(j.hasNext(), is(true));
    UserDetail user2 = j.next();
    assertThat(user2.getLoginName(), is(equalTo(LOGIN_NAME2)));        
    assertThat(j.hasNext(), is(false));
  }
  
  @Test
  public void editGroup() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { { 
      oneOf(editorFactory).newEditor(with(id));
      will(returnValue(editor));
    } });
    
    assertThat(service.editGroup(id), is(sameInstance((GroupEditor) editor)));
  }
  

  @Test
  public void testSaveGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
    } });
    
    service.saveGroup(editor, errors);
  }
  
}
