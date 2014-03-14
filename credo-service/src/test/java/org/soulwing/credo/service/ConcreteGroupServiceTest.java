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

import java.util.Collections;
import java.util.Iterator;

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
import org.soulwing.credo.service.group.ConfigurableGroupEditor;
import org.soulwing.credo.service.group.GroupEditorFactory;

/**
 * Unit tests for {@link ConcreteGroupService}.
 *
 * @author Carl Harris
 */
public class ConcreteGroupServiceTest {

  private static final String GROUP_NAME = "groupName";

  private static final String LOGIN_NAME = "someUser";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupEditorFactory editorFactory;
  
  @Mock
  private UserGroupRepository groupRepository;
  
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
  
  @Mock
  private UserGroupMember member;
  
  private ConcreteGroupService service = new ConcreteGroupService();
  
  @Before
  public void setUp() throws Exception {
    service.editorFactory = editorFactory;
    service.groupRepository = groupRepository;
    service.memberRepository = memberRepository;
    service.userContextService = userContextService;    
  }
  
  @Test
  public void testNewEditor() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editorFactory).newEditor();
      will(returnValue(editor));
    } });
    
    assertThat(service.newGroup(), is(sameInstance((GroupEditor) editor)));
  }
  
  @Test
  public void testFindAllGroups() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByLoginName(with(LOGIN_NAME));
      will(returnValue(Collections.singleton(group)));
      exactly(2).of(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(memberRepository).findAllMembers(with(GROUP_NAME));
      will(returnValue(Collections.singleton(member)));
      oneOf(member).getUser();
      will(returnValue(profile));
      oneOf(profile).getLoginName();
      will(returnValue(LOGIN_NAME));
    } });
    
    Iterator<GroupDetail> i = service.findAllGroups().iterator();
    assertThat(i.hasNext(), is(true));
    GroupDetail group = i.next();
    assertThat(group.getName(), is(equalTo(GROUP_NAME)));
    
    Iterator<UserDetail> j = group.getMembers().iterator();
    assertThat(j.hasNext(), is(true));
    UserDetail user = j.next();
    assertThat(user.getLoginName(), is(equalTo(LOGIN_NAME)));        
  }
  
  @Test
  public void testSaveGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
    } });
    
    service.saveGroup(editor, errors);
  }
  
}
