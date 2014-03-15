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
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

/**
 * Unit tests for {@link EditGroupBean}.
 *
 * @author Carl Harris
 */
public class EditGroupBeanTest {

  private static final long GROUP_ID = -1L;
  private static final Password PASSWORD = new Password(new char[0]);
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private GroupEditor editor;
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private Errors errors;
  
  private EditGroupBean bean = new EditGroupBean();
  
  @Before
  public void setUp() throws Exception {
    bean.groupService = groupService;
    bean.errors = errors;
    bean.conversation = conversation;
    bean.getPasswordFormBean().setPassword(PASSWORD);
  }
  
  @Test
  public void testCreateEditor() throws Exception {
    context.checking(editGroupExpectations(returnValue(editor)));
    context.checking(beginConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.createEditor(), is(nullValue()));
    assertThat(bean.getEditor(), is(sameInstance(editor)));
  }

  @Test
  public void testCreateEditorWhenNoId() throws Exception {
    context.checking(errorExpectations("id", "Required"));
    assertThat(bean.createEditor(), is(nullValue()));
  }
  
  @Test
  public void testCreateEditorWhenGroupNotFound() throws Exception {
    context.checking(editGroupExpectations(
        throwException(new NoSuchGroupException())));
    context.checking(errorExpectations("id", "NotFound"));    
    bean.setId(GROUP_ID);
    assertThat(bean.createEditor(), is(nullValue()));
  }

  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(saveGroupExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    bean.setEditor(editor);
    assertThat(bean.save(), is(equalTo(EditGroupBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test
  public void testSaveWhenGroupEditException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new GroupEditException())));
    bean.setEditor(editor);
    assertThat(bean.save(), is(nullValue()));
  }

  @Test(expected = RuntimeException.class)
  public void testSaveWhenNoSuchGroupException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new NoSuchGroupException())));
    context.checking(endConversationExpectations());
    bean.setEditor(editor);
    bean.save();
  }

  @Test
  public void testSaveWhenPassphraseException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new PassphraseException())));
    bean.setEditor(editor);
    assertThat(bean.save(), is(equalTo(EditGroupBean.PASSWORD_OUTCOME_ID)));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), is(equalTo(EditGroupBean.CANCEL_OUTCOME_ID)));
  }
  
  private Expectations editGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(groupService).editGroup(GROUP_ID);
      will(outcome);
    } };
  }
  
  private Expectations saveGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(editor).setPassword(with(PASSWORD));
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
      will(outcome);
    } };
  }
  
  private Expectations beginConversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }

  private Expectations endConversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } };
  }

  private Expectations errorExpectations(final String id, 
      final String message) {
    return new Expectations() { { 
      oneOf(errors).addError(with(id), with(containsString(message)),
          with(emptyArray()));
    } };
  }
}
