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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserContextService;
import org.soulwing.credo.service.UserDetail;

/**
 * Unit tests for {@link GroupServiceBean}.
 *
 * @author Carl Harris
 */
public class GroupServiceBeanTest {

  private static final Long GROUP_ID1 = -1L;

  private static final Long GROUP_ID2 = -2L;

  private static final String GROUP_NAME1 = "groupName1";

  private static final String GROUP_NAME2 = "groupName2";
  
  private static final String LOGIN_NAME1 = "user1";
  
  private static final String LOGIN_NAME2 = "user2";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private CredentialRequestRepository requestRepository;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private Credential credential;
  
  @Mock
  private UserGroup group1;

  @Mock
  private UserGroup group2;
  
  @Mock
  private UserGroupMember member1;
  
  @Mock
  private UserGroupMember member2;
  
  @Mock
  private UserProfile profile1;
  
  @Mock
  private UserProfile profile2;
  

  private GroupServiceBean service = new GroupServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.requestRepository = requestRepository;
    service.groupRepository = groupRepository;
    service.memberRepository = memberRepository;
    service.userContextService = userContextService;    
  }
  
  @Test
  public void testFindAllGroupsWhenNoDescendants() throws Exception {
    final List<UserGroupMember> members = Arrays.asList(new UserGroupMember[] { 
        member1, member2
    });
   
    context.checking(new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(memberRepository).findByLoginName(with(LOGIN_NAME1));
      will(returnValue(members));
      oneOf(member1).getGroup();
      will(returnValue(group1));
      oneOf(member2).getGroup();
      will(returnValue(group1));
      allowing(group1).getName();
      will(returnValue(GROUP_NAME1));
      oneOf(member1).getUser();
      will(returnValue(profile1));
      oneOf(profile1).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(member2).getUser();
      will(returnValue(profile2));
      oneOf(profile2).getLoginName();
      will(returnValue(LOGIN_NAME2));
      oneOf(groupRepository).findDescendants(with(group1));
      will(returnValue(Collections.emptyList()));
    } });
    
    context.checking(resolveInUseExpectations());
    Iterator<GroupDetail> i = service.findAllGroups().iterator();
    assertThat(i.hasNext(), is(true));
    GroupDetail group = i.next();
    assertThat(group.getName(), is(equalTo(GROUP_NAME1)));
    
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
  public void testFindAllGroupsWithDescendants() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(memberRepository).findByLoginName(with(LOGIN_NAME1));
      will(returnValue(Collections.singleton(member1)));
      oneOf(member1).getGroup();
      will(returnValue(group1));
      oneOf(member1).getUser();
      will(returnValue(profile1));
      oneOf(groupRepository).findDescendants(with(group1));
      will(returnValue(Collections.singletonList(group2)));
      oneOf(group2).getMembers();
      will(returnValue(Collections.singleton(member2)));
      oneOf(member2).getUser();
      will(returnValue(profile2));
      
      allowing(group1).getName();
      will(returnValue(GROUP_NAME1));
      allowing(group2).getName();
      will(returnValue(GROUP_NAME2));
      allowing(profile1).getLoginName();
      will(returnValue(LOGIN_NAME1));
      allowing(profile2).getLoginName();
      will(returnValue(LOGIN_NAME2));
    } });
    
    context.checking(resolveInUseExpectations());
    Collection<GroupDetail> groups = service.findAllGroups();
    System.out.println(groups);
    Iterator<GroupDetail> i = groups.iterator();
    assertThat(i.hasNext(), is(true));
    GroupDetail group = i.next();
    assertThat(group.getName(), is(equalTo(GROUP_NAME1)));
    
    Iterator<UserDetail> j = group.getMembers().iterator();
    assertThat(j.hasNext(), is(true));
    UserDetail user1 = j.next();
    assertThat(user1.getLoginName(), is(equalTo(LOGIN_NAME1)));        
    assertThat(j.hasNext(), is(false));

    assertThat(i.hasNext(), is(true));
    group = i.next();
    assertThat(group.getName(), is(equalTo(GROUP_NAME2)));
    
    j = group.getMembers().iterator();
    assertThat(j.hasNext(), is(true));
    UserDetail user2 = j.next();
    assertThat(user2.getLoginName(), is(equalTo(LOGIN_NAME2)));
    assertThat(j.hasNext(), is(false));
    
    assertThat(i.hasNext(), is(false));
  }
  
  private Expectations resolveInUseExpectations() throws Exception {
    return new Expectations() { { 
      allowing(group1).getId();
      will(returnValue(GROUP_ID1));
      allowing(credentialRepository).findAllByOwnerId(with(GROUP_ID1));
      will(returnValue(Collections.emptyList()));
      allowing(group2).getId();
      will(returnValue(GROUP_ID2));
      allowing(credentialRepository).findAllByOwnerId(with(GROUP_ID2));
      will(returnValue(Collections.emptyList()));
      allowing(group1).getId();
      will(returnValue(GROUP_ID1));
      allowing(requestRepository).findAllByOwnerId(with(GROUP_ID1));
      will(returnValue(Collections.emptyList()));
      allowing(group2).getId();
      will(returnValue(GROUP_ID2));
      allowing(requestRepository).findAllByOwnerId(with(GROUP_ID2));
      will(returnValue(Collections.emptyList()));
    } };
  }
  
  @Test
  public void testGroupIsExistingGroup() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME1), 
          with(LOGIN_NAME1));
      will(returnValue(group1));
      oneOf(memberRepository).findByGroupAndLoginName(
          with(group1), with(LOGIN_NAME1));
      will(returnValue(member1));
    } });
    
    assertThat(service.isExistingGroup(GROUP_NAME1), is(true));
  }

  @Test
  public void testGroupIsNotExistingGroup() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME1), 
          with(LOGIN_NAME1));
      will(returnValue(null));
    } });
    
    assertThat(service.isExistingGroup(GROUP_NAME1), is(false));
  }

  @Test(expected = GroupAccessException.class)
  public void testGroupIsExistingGroupAndUserIsNotMember() throws Exception {
    context.checking(new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME1));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME1), 
          with(LOGIN_NAME1));
      will(returnValue(group1));
      oneOf(memberRepository).findByGroupAndLoginName(
          with(group1), with(LOGIN_NAME1));
      will(returnValue(null));
    } });
    
    service.isExistingGroup(GROUP_NAME1);
  }


}
