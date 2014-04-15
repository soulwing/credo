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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.UserContextService;

/**
 * Unit tests for {@link GroupResolverBean}.
 *
 * @author Carl Harris
 */
public class GroupResolverBeanTest {
  
  private static final String GROUP_NAME = "someGroup";
  
  private static final String LOGIN_NAME = "someUser";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private CreateGroupService groupService;
  
  @Mock
  private UserContextService userContextService;

  @Mock
  private GroupEditor editor;
  
  @Mock
  private Errors errors;
  
  @Mock
  private UserGroup group;
  
  private GroupResolverBean bean = new GroupResolverBean();
  
  @Before
  public void setUp() throws Exception {
    bean.groupRepository = groupRepository;
    bean.groupService = groupService;
    bean.userContextService = userContextService;
  }

  @Test
  public void testResolveWhenGroupFound() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    assertThat(bean.resolveGroup(GROUP_NAME, errors), is(sameInstance(group)));
  }
  
  @Test
  public void testResolveWhenGroupNotFound() throws Exception {
    context.checking(findGroupExpectations(returnValue(null)));
    context.checking(createGroupExpectations());
    context.checking(findGroupExpectations(returnValue(group)));
    assertThat(bean.resolveGroup(GROUP_NAME, errors), is(sameInstance(group)));
  }
  
  private Expectations findGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { {
      oneOf(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME), 
          with(LOGIN_NAME));
      will(outcome);
    } };
  }
  
  private Expectations createGroupExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(groupService).newGroup();
      will(returnValue(editor));
      oneOf(editor).setName(GROUP_NAME);
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
    } };
  }
}
