/*
 * File created on Mar 15, 2014 
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
package org.soulwing.credo.facelets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import javax.enterprise.context.Conversation;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupDetail;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.NoSuchGroupException;

/**
 * Unit tests for {@link RemoveGroupBean}.
 *
 * @author Carl Harris
 */
public class RemoveGroupBeanTest {

  private static final Long GROUP_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private GroupDetail group;
  
  @Mock
  private Errors errors;
  
  @Mock
  private Conversation conversation;
  
  private RemoveGroupBean bean = new RemoveGroupBean();
  
  @Before
  public void setUp() throws Exception {
    bean.groupService = groupService;
    bean.errors = errors;
    bean.conversation = conversation;
  }

  @Test
  public void testFindGroupSuccess() throws Exception {
    context.checking(findGroupExpectations(returnValue(group)));
    context.checking(beginConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.findGroup(), is(nullValue()));
    assertThat(bean.getGroup(), is(sameInstance(group)));
  }

  @Test
  public void testFindGroupWhenNoSuchGroup() throws Exception {
    context.checking(findGroupExpectations(
        throwException(new NoSuchGroupException())));
    bean.setId(GROUP_ID);
    assertThat(bean.findGroup(), 
        is(equalTo(RemoveGroupBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testFindGroupWhenNoId() throws Exception {
    context.checking(errorExpectations("id", "Required"));
    bean.setId(null);
    assertThat(bean.findGroup(), 
        is(equalTo(RemoveGroupBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), is(equalTo(RemoveGroupBean.CANCEL_OUTCOME_ID)));
  }

  @Test
  public void testRemoveSuccess() throws Exception {
    context.checking(removeGroupExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.remove(), is(equalTo(RemoveGroupBean.SUCCESS_OUTCOME_ID)));
  }

  @Test
  public void testRemoveWhenNoSuchGroupException() throws Exception {
    context.checking(removeGroupExpectations(
        throwException(new NoSuchGroupException())));
    context.checking(endConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.remove(), is(equalTo(RemoveGroupBean.FAILURE_OUTCOME_ID)));
  }
  
  @Test
  public void testRemoveWhenGroupEditException() throws Exception {
    context.checking(removeGroupExpectations(
        throwException(new GroupEditException())));
    context.checking(endConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.remove(), is(equalTo(RemoveGroupBean.FAILURE_OUTCOME_ID)));
  }

  private Expectations findGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(groupService).findGroup(with(GROUP_ID));
      will(outcome);
    } };
  }
  
  private Expectations removeGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(groupService).removeGroup(with(GROUP_ID),
          with(same(errors)));
      will(outcome);
    } };
  }

  private Expectations errorExpectations(final String id, 
      final String message) {
    return new Expectations() { { 
      oneOf(errors).addError(with(id), with(containsString(message)),
          with(emptyArray()));
    } };
  }

  private Expectations beginConversationExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }

  private Expectations endConversationExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } };
  }

}

