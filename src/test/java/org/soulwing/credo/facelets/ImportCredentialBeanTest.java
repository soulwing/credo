/*
 * File created on Feb 13, 2014 
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.ImportDetails;
import org.soulwing.credo.service.credential.ImportException;
import org.soulwing.credo.service.credential.ImportService;
import org.soulwing.credo.service.group.NoSuchGroupException;
import org.soulwing.credo.testing.JUnitRuleClassImposterizingMockery;

/**
 * Unit tests for {@link ImportCredentialBean}.
 *
 * @author Carl Harris
 */
public class ImportCredentialBeanTest {

  private static final String GROUP_NAME = "someGroup";

  private static final Password PASSPHRASE = Password.EMPTY;
  
  @Rule
  public JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private Errors errors;
  
  @Mock
  private ImportService importService;
  
  @Mock
  private ImportDetails details;
  
  @Mock
  private Credential credential;
  
  @Mock
  private FacesContext facesContext;
  
  private ImportCredentialBean bean = new ImportCredentialBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.errors = errors;
    bean.importService = importService;
    bean.facesContext = facesContext;
    bean.passwordEditor = new PasswordFormEditor();
    bean.fileUploadEditor = new FileUploadEditor();
    bean.editor = new DelegatingCredentialEditor<ImportDetails>();
    bean.setPassphrase(PASSPHRASE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadWithNoFilesSelected() throws Exception {
    context.checking(conversationExpectations());
    context.checking(new Expectations() { {
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(any(List.class)),
          with(PASSPHRASE), with(errors));
      will(throwException(new ImportException()));
    } });
    
    bean.getFileUploadEditor().setFile0(null);
    bean.getFileUploadEditor().setFile1(null);
    bean.getFileUploadEditor().setFile2(null);
    assertThat(bean.upload(), nullValue());
  }
  
  @Test
  public void testUploadSuccessWithFile0Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(prepareImportExpectations(file, returnValue(details)));
    
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.upload(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), is(sameInstance(details)));
  }

  @Test
  public void testUploadSuccessWithFile1Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(prepareImportExpectations(file, 
        returnValue(details)));
    
    bean.getFileUploadEditor().setFile1(file);
    assertThat(bean.upload(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), is(sameInstance(details)));
  }
    
  @Test
  public void testUploadSuccessWithFile2Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(prepareImportExpectations(file, returnValue(details)));
    bean.getFileUploadEditor().setFile2(file);
    assertThat(bean.upload(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), sameInstance(details));
  }

  @Test
  public void testUploadPassphraseRequired() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(prepareImportExpectations(file, 
        throwException(new PassphraseException())));
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.upload(), 
        is(equalTo(ImportCredentialBean.PASSPHRASE_OUTCOME_ID)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadError() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(PASSPHRASE), with(errors));
      will(throwException(new ImportException()));
    } });
    
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.upload(), nullValue());
  }

  @SuppressWarnings("unchecked")
  private Expectations prepareImportExpectations(final Part file,
      final Action preparationOutcome) 
      throws Exception {
    return new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(PASSPHRASE), with(errors));    
      will(preparationOutcome);
    } };
  }
    
  private Expectations conversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }
  
  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(false), with(same(errors)));
      oneOf(conversation).end();
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), equalTo(ImportCredentialBean.SUCCESS_OUTCOME_ID));    
  }

  @Test
  public void testSaveImportError() throws Exception {

    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(false), with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), nullValue());    
  }

  @Test
  public void testSaveGroupAccessDenied() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(false), with(same(errors)));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), 
        is(equalTo(ImportSignedCertificateBean.FAILURE_OUTCOME_ID)));    
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(new Expectations() { {
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } });
    
    assertThat(bean.cancel(), equalTo(ImportCredentialBean.CANCEL_OUTCOME_ID));
  }
  
  @Test
  public void testProtectSuccess() throws Exception {
    context.checking(protectionExpectations(returnValue(null)));    
    bean.setDetails(details);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.CONFIRM_OUTCOME_ID)));    
  }
  
  @Test
  public void testProtectOwnerNotFound() throws Exception {
    context.checking(protectionExpectations(
        throwException(new NoSuchGroupException())));    
    bean.setDetails(details);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));        
  }

  @Test
  public void testProtectPasswordIncorrect() throws Exception {
    context.checking(protectionExpectations(
        throwException(new PassphraseException())));
    context.checking(new Expectations() { { 
      oneOf(details).getOwner();
      will(returnValue(GROUP_NAME));
    } });
    bean.setDetails(details);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.PASSWORD_OUTCOME_ID)));
    assertThat(bean.getPasswordEditor().getGroupName(),
        is(equalTo(GROUP_NAME)));
  }

  @Test
  public void testProtectAccessDenied() throws Exception {
    context.checking(protectionExpectations(
        throwException(new GroupAccessException(GROUP_NAME))));
    bean.setDetails(details);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));     
  }

  private Expectations protectionExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(importService).createCredential(with(same(details)), 
          with(same(bean.getPasswordEditor())), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
}
