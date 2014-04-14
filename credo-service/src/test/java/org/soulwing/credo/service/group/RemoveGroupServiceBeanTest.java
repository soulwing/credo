/*
 * File created on Apr 14, 2014 
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import javax.persistence.PersistenceException;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserContextService;

/**
 * Unit tests for {@link RemoveGroupServiceBean}.
 *
 * @author Carl Harris
 */
public class RemoveGroupServiceBeanTest {

  private static final String GROUP_NAME = "someGroup";

  private static final String LOGIN_NAME = "someUser";

  private static final long GROUP_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserGroupMember member;
  
  @Mock
  private Errors errors;
  
  private RemoveGroupServiceBean service = new RemoveGroupServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.groupRepository = groupRepository;
    service.memberRepository = memberRepository;
    service.userContextService = userContextService;    
  }
  
  @Test
  public void testFindGroup() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    assertThat(service.findGroup(GROUP_ID), is(instanceOf(GroupDetail.class)));
  }

  @Test(expected = NoSuchGroupException.class)
  public void testFindGroupWhenNotFound() throws Exception {
    context.checking(findGroupExpectations(returnValue(null)));
    service.findGroup(GROUP_ID);
  }
  
  @Test
  public void testRemoveGroupSuccess() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    context.checking(findMemberExpectations(returnValue(member)));
    context.checking(removeGroupExpectations(returnValue(null)));
    service.removeGroup(GROUP_ID, errors);
  }

  @Test
  public void testRemoveGroupWhenNotFound() throws Exception {
    context.checking(findGroupExpectations(returnValue(null)));
    service.removeGroup(GROUP_ID, errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testRemoveGroupWhenNotMember() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    context.checking(findMemberExpectations(returnValue(null)));
    context.checking(new Expectations() { { 
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(errors).addError(with(containsString("AccessDenied")),
          (Object[]) with(arrayContaining(GROUP_NAME)));
    } });
    
    service.removeGroup(GROUP_ID, errors);
  }

  @Test(expected = GroupException.class)
  public void testRemoveGroupWhenInUse() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    context.checking(findMemberExpectations(returnValue(member)));
    context.checking(removeGroupExpectations(
        throwException(new PersistenceException())));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with(containsString("InUse")),
          (Object[]) with(arrayContaining(GROUP_ID)));
    } });
    
    service.removeGroup(GROUP_ID, errors);
  }

  private Expectations findGroupExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(groupRepository).findById(with(GROUP_ID));
      will(outcome);
    } };
  }

  private Expectations findMemberExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(memberRepository).findByGroupAndLoginName(with(same(group)), 
          with(LOGIN_NAME));
      will(outcome);
    } };
  }

  private Expectations removeGroupExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(groupRepository).remove(group);
      will(outcome);
    } };  
  }
  
}
