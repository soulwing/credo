/*
 * File created on Mar 19, 2014 
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import javax.enterprise.context.Conversation;
import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.group.NoSuchGroupException;
import org.soulwing.credo.service.request.CreateCredentialRequestService;
import org.soulwing.credo.service.request.CredentialRequestEditor;
import org.soulwing.credo.service.request.CredentialRequestException;
import org.soulwing.credo.testing.JUnitRuleClassImposterizingMockery;

/**
 * Unit tests for {@link CreateCredentialRequestBean}.
 *
 * @author Carl Harris
 */
public class CreateCredentialRequestBeanTest {

  private static final String GROUP_NAME = "groupName";

  private static final long CREDENTIAL_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();
  
  @Mock
  private CreateCredentialRequestService requestService;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private Errors errors;
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private CredentialRequestEditor editor;
  
  @Mock
  private Conversation conversation;
  
  private CreateCredentialRequestBean bean = new CreateCredentialRequestBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.requestService = requestService;
    bean.editor = new DelegatingCredentialRequestEditor();
    bean.passwordEditor = new PasswordFormEditor();
    bean.errors = errors;
    bean.facesContext = facesContext;
  }

  @Test
  public void testFindCredentialWhenNotSpecified() throws Exception {
    context.checking(beginConversationExpectations());
    context.checking(new Expectations() { { 
      oneOf(requestService).createEditor();
      will(returnValue(editor));
    } });
    
    bean.setCredentialId(null);
    assertThat(bean.findCredential(), 
        is(equalTo(CreateCredentialRequestBean.SUBJECT_OUTCOME_ID)));
    assertThat(bean.editor.getDelegate(), is(sameInstance(editor)));
  }

  @Test
  public void testFindCredentialWhenNoSuchCredential() throws Exception {
    context.checking(existingCredentialEditorExpectations(
        throwException(new NoSuchCredentialException())));
    bean.setCredentialId(CREDENTIAL_ID);
    assertThat(bean.findCredential(), 
        is(equalTo(CreateCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testFindCredentialSuccess() throws Exception {
    context.checking(beginConversationExpectations());
    context.checking(existingCredentialEditorExpectations(
        returnValue(editor)));
    
    bean.setCredentialId(CREDENTIAL_ID);
    assertThat(bean.findCredential(),
        is(equalTo(CreateCredentialRequestBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getEditor().getDelegate(), is(sameInstance(editor)));
  }

  private Expectations existingCredentialEditorExpectations(
      final Action outcome) throws Exception {
    return new Expectations() { { 
      oneOf(requestService).createEditor(with(CREDENTIAL_ID), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  @Test
  public void testPrepareWhenNoSuchGroupException() throws Exception {
    context.checking(createSigningRequestExpectations(
        throwException(new NoSuchGroupException())));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateCredentialRequestBean.DETAILS_OUTCOME_ID)));
  }

  @Test
  public void testPrepareWhenPassphraseException() throws Exception {
    context.checking(createSigningRequestExpectations(
        throwException(new PassphraseException())));
    bean.getEditor().setDelegate(editor);
    context.checking(new Expectations() { { 
      oneOf(editor).getOwner();
      will(returnValue(GROUP_NAME));
    } });
    assertThat(bean.prepare(), 
        is(equalTo(CreateCredentialRequestBean.PASSWORD_OUTCOME_ID)));
  }

  @Test
  public void testPrepareWhenGroupAccessException() throws Exception {
    context.checking(createSigningRequestExpectations(
        throwException(new GroupAccessException("groupName"))));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateCredentialRequestBean.DETAILS_OUTCOME_ID)));
  }

  @Test
  public void testPrepareWhenSigningRequestException() throws Exception {
    context.checking(endConversationExpectations());
    context.checking(createSigningRequestExpectations(
        throwException(new CredentialRequestException())));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateCredentialRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test 
  public void testPrepareSuccess() throws Exception {
    context.checking(createSigningRequestExpectations(
        returnValue(request)));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(),
        is(equalTo(CreateCredentialRequestBean.CONFIRM_OUTCOME_ID)));
    assertThat(bean.getSigningRequest(), is(sameInstance(request)));
  }
  
  private Expectations createSigningRequestExpectations(
      final Action outcome) throws Exception {
    return new Expectations() { { 
      oneOf(requestService).createRequest(
          with(same(editor)), 
          with(same(bean.getPasswordEditor())), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  @Test  
  public void testSaveAction() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestService).saveRequest(
          with(same(request)), with(errors));
    } });
    
    bean.setSigningRequest(request);
    assertThat(bean.save(), 
        is(equalTo(CreateCredentialRequestBean.SUCCESS_OUTCOME_ID)));
  }

  @Test  
  public void testSaveWhenGroupAccessDenied() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestService).saveRequest(         
          with(same(request)), with(errors));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    bean.setSigningRequest(request);
    assertThat(bean.save(), 
        is(equalTo(CreateCredentialRequestBean.DETAILS_OUTCOME_ID)));
  }
  

  @Test
  public void testCancelAction() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), 
        is(equalTo(CreateCredentialRequestBean.CANCEL_OUTCOME_ID)));
  }

  @Test
  public void testDownloadAction() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestService).downloadRequest(
          with(same(request)), 
          with(any(FacesFileDownloadResponse.class)));
      oneOf(facesContext).responseComplete();
    } });
    
    bean.setSigningRequest(request);
    assertThat(bean.download(), is(nullValue()));
  }
  
  private Expectations beginConversationExpectations() { 
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
      oneOf(conversation).setTimeout(
          with(CreateCredentialRequestBean.CONVERSATION_TIMEOUT));
    } };
  }

  private Expectations endConversationExpectations() { 
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } };
  }

}

