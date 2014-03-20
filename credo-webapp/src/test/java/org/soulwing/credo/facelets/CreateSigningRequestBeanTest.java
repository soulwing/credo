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
import org.soulwing.credo.SigningRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.SigningRequestEditor;
import org.soulwing.credo.service.SigningRequestException;
import org.soulwing.credo.service.SigningRequestService;

/**
 * Unit tests for {@link CreateSigningRequestBean}.
 *
 * @author Carl Harris
 */
public class CreateSigningRequestBeanTest {

  private static final String GROUP_NAME = "groupName";

  private static final long CREDENTIAL_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private SigningRequestService signingRequestService;
  
  @Mock
  private SigningRequest signingRequest;
  
  @Mock
  private Errors errors;
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private SigningRequestEditor editor;
  
  @Mock
  private Conversation conversation;
  
  private CreateSigningRequestBean bean = new CreateSigningRequestBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.signingRequestService = signingRequestService;
    bean.editor = new DelegatingCredentialEditor<SigningRequestEditor>();
    bean.passwordEditor = new PasswordFormEditor();
    bean.errors = errors;
    bean.facesContext = facesContext;
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFindCredentialWhenNotSpecified() throws Exception {
    bean.setCredentialId(null);
    bean.findCredential();
  }

  @Test
  public void testFindCredentialWhenNoSuchCredential() throws Exception {
    context.checking(existingCredentialEditorExpectations(
        throwException(new NoSuchCredentialException())));
    bean.setCredentialId(CREDENTIAL_ID);
    assertThat(bean.findCredential(), 
        is(equalTo(CreateSigningRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testFindCredentialSuccess() throws Exception {
    context.checking(beginConversationExpectations());
    context.checking(existingCredentialEditorExpectations(
        returnValue(editor)));
    
    bean.setCredentialId(CREDENTIAL_ID);
    assertThat(bean.findCredential(),
        is(equalTo(CreateSigningRequestBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getEditor().getDelegate(), is(sameInstance(editor)));
  }

  private Expectations existingCredentialEditorExpectations(
      final Action outcome) throws Exception {
    return new Expectations() { { 
      oneOf(signingRequestService).createEditor(with(CREDENTIAL_ID), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  @Test
  public void testPasswordAction() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).getOwner();
      will(returnValue(GROUP_NAME));
    } });
    
    bean.getEditor().setDelegate(editor);
    assertThat(bean.password(), 
        is(equalTo(CreateSigningRequestBean.PASSWORD_OUTCOME_ID)));
    assertThat(bean.getPasswordEditor().getGroupName(),
        is(equalTo(GROUP_NAME)));
  }

  @Test
  public void testPrepareWhenPassphraseException() throws Exception {
    context.checking(createSigningRequestExpectations(
        throwException(new PassphraseException())));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateSigningRequestBean.PASSWORD_OUTCOME_ID)));
  }

  @Test
  public void testPrepareWhenGroupAccessException() throws Exception {
    context.checking(createSigningRequestExpectations(
        throwException(new GroupAccessException("some message"))));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateSigningRequestBean.DETAILS_OUTCOME_ID)));
  }

  @Test
  public void testPrepareWhenSigningRequestException() throws Exception {
    context.checking(endConversationExpectations());
    context.checking(createSigningRequestExpectations(
        throwException(new SigningRequestException())));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(), 
        is(equalTo(CreateSigningRequestBean.FAILURE_OUTCOME_ID)));
  }

  @Test 
  public void testPrepareSuccess() throws Exception {
    context.checking(createSigningRequestExpectations(
        returnValue(signingRequest)));
    bean.getEditor().setDelegate(editor);
    assertThat(bean.prepare(),
        is(equalTo(CreateSigningRequestBean.CONFIRM_OUTCOME_ID)));
    assertThat(bean.getSigningRequest(), is(sameInstance(signingRequest)));
  }
  
  private Expectations createSigningRequestExpectations(
      final Action outcome) throws Exception {
    return new Expectations() { { 
      oneOf(signingRequestService).createSigningRequest(
          with(same(editor)), 
          with(same(bean.getPasswordEditor())), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  @Test  
  public void testSaveAction() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(signingRequestService).saveSigningRequest(
          with(same(signingRequest)));
    } });
    
    bean.setSigningRequest(signingRequest);
    assertThat(bean.save(), 
        is(equalTo(CreateSigningRequestBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test
  public void testCancelAction() throws Exception {
    context.checking(endConversationExpectations());
    assertThat(bean.cancel(), 
        is(equalTo(CreateSigningRequestBean.CANCEL_OUTCOME_ID)));
  }

  @Test
  public void testDownloadAction() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(signingRequestService).downloadSigningRequest(
          with(same(signingRequest)), 
          with(any(FacesFileDownloadResponse.class)));
      oneOf(facesContext).responseComplete();
    } });
    
    bean.setSigningRequest(signingRequest);
    assertThat(bean.download(), is(nullValue()));
  }
  
  private Expectations beginConversationExpectations() { 
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
      oneOf(conversation).setTimeout(
          with(CreateSigningRequestBean.CONVERSATION_TIMEOUT));
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

