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
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.group.EditException;
import org.soulwing.credo.service.group.EditGroupService;
import org.soulwing.credo.service.group.GroupEditor;
import org.soulwing.credo.service.group.NoSuchGroupException;

/**
 * Unit tests for {@link EditGroupBean}.
 *
 * @author Carl Harris
 */
public class EditGroupBeanTest {

  private static final long GROUP_ID = -1L;
  private static final String GROUP_NAME = "groupName";
  private static final Password PASSWORD = Password.EMPTY;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private EditGroupService groupService;
  
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
    bean.editor = new DelegatingGroupEditor();
    bean.editor.setDelegate(this.editor);
    bean.passwordEditor = new PasswordFormEditor();
    bean.getPasswordEditor().setPassword(PASSWORD);
  }
  
  @Test
  public void testCreateEditor() throws Exception {
    context.checking(editGroupExpectations(returnValue(editor)));
    context.checking(beginConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.createEditor(), is(nullValue()));
    assertThat(bean.getEditor().getDelegate(), is(sameInstance(editor)));
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
    context.checking(endConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.createEditor(), 
        is(equalTo(EditGroupBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(saveGroupExpectations(returnValue(null)));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), is(equalTo(EditGroupBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test
  public void testSaveWhenGroupEditException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new EditException())));
    assertThat(bean.save(), is(nullValue()));
  }

  @Test
  public void testSaveWhenNoSuchGroupException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new NoSuchGroupException())));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), is(equalTo(EditGroupBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveWhenGroupAccessException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new GroupAccessException("message"))));
    context.checking(endConversationExpectations());
    assertThat(bean.save(), is(equalTo(EditGroupBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testSaveWhenPassphraseException() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new PassphraseException())));
    assertThat(bean.save(), is(equalTo(EditGroupBean.PASSWORD_OUTCOME_ID)));
  }

  @Test
  public void testSaveWhenMergeConflict() throws Exception {
    context.checking(saveGroupExpectations(
        throwException(new MergeConflictException())));
    context.checking(editGroupExpectations(returnValue(editor)));
    context.checking(beginConversationExpectations());
    bean.setId(GROUP_ID);
    assertThat(bean.save(), is(nullValue()));
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), is(equalTo(EditGroupBean.CANCEL_OUTCOME_ID)));
  }
  
  private Expectations editGroupExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      allowing(editor).getName();
      will(returnValue(GROUP_NAME));
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
