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
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
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
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportService;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

/**
 * Unit tests for {@link ImportSignedCertificateBean}.
 *
 * @author Carl Harris
 */
public class ImportSignedCertificateBeanTest {
  
  private static final String GROUP_NAME = "someGroup";

  private static final long REQUEST_ID = -1L;

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() { {
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private Errors errors;
  
  @Mock
  private ImportService importService;
  
  @Mock
  private ImportDetails details;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private Credential credential;
  
  @Mock
  private FacesContext facesContext;
  
  private ImportSignedCertificateBean bean = new ImportSignedCertificateBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.errors = errors;
    bean.importService = importService;
    bean.facesContext = facesContext;
    bean.fileUploadEditor = new FileUploadEditor();
    bean.passwordEditor = new PasswordFormEditor();
    bean.editor = new DelegatingCredentialEditor<ImportDetails>();
  }

  @Test
  public void testFindRequestWhenIdNotSpecified() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with("requestId"), with("requestIdIsRequired"),
          with(emptyArray()));
    } });
    bean.setRequestId(null);
    assertThat(bean.findRequest(), 
        is(equalTo(ImportSignedCertificateBean.FAILURE_OUTCOME_ID)));
  }
  
  @Test
  public void testFindRequestWhenNotFound() throws Exception {
    context.checking(findRequestExpectations(
        throwException(new NoSuchCredentialException())));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with("requestId"), with("requestNotFound"), 
          (Object[]) with(arrayContaining(REQUEST_ID)));
    } });
    bean.setRequestId(REQUEST_ID);
    assertThat(bean.findRequest(), 
        is(equalTo(ImportSignedCertificateBean.FAILURE_OUTCOME_ID)));
  }
  
  @Test
  public void testFindRequest() throws Exception { 
    context.checking(findRequestExpectations(returnValue(request)));
    context.checking(beginConversationExpectations());
    bean.setRequestId(REQUEST_ID);
    assertThat(bean.findRequest(), is(nullValue()));
    assertThat(bean.getRequest(), is(sameInstance(request)));
  }
  
  private Expectations findRequestExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(importService).findRequestById(REQUEST_ID);
      will(outcome);
    } };
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testPrepareWithNoFilesSelected() throws Exception {
    context.checking(new Expectations() { {
      oneOf(importService).prepareImport(
          with(same(request)), 
          (List<FileContentModel>) with(any(List.class)),
          with(same(bean.getPasswordEditor())),
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile0(null);
    bean.getFileUploadEditor().setFile1(null);
    bean.getFileUploadEditor().setFile2(null);
    assertThat(bean.prepare(), 
        is(equalTo(ImportSignedCertificateBean.RESTART_OUTCOME_ID)));
  }
  
  @Test
  public void testPrepareSuccessWithFile0Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(prepareImportExpectations(file, returnValue(details)));
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.prepare(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), is(sameInstance(details)));
  }

  @Test
  public void testPrepareSuccessWithFile1Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(prepareImportExpectations(file, 
        returnValue(details)));
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile1(file);
    assertThat(bean.prepare(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), is(sameInstance(details)));
  }
    
  @Test
  public void testPrepareSuccessWithFile2Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(prepareImportExpectations(file, returnValue(details)));
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile2(file);
    assertThat(bean.prepare(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));
    assertThat(bean.getDetails(), is(sameInstance(details)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPrepareError() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          with(same(request)),
          (List<FileContentModel>) with(not(empty())),
          with(same(bean.getPasswordEditor())),
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.prepare(), 
        is(equalTo(ImportSignedCertificateBean.RESTART_OUTCOME_ID)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPrepareWhenGroupAccessException() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          with(same(request)),
          (List<FileContentModel>) with(not(empty())),
          with(same(bean.getPasswordEditor())),
          with(same(errors)));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.prepare(), 
        is(equalTo(ImportSignedCertificateBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPrepareWhenPasswordIncorrect() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          with(same(request)),
          (List<FileContentModel>) with(not(empty())),
          with(same(bean.getPasswordEditor())),
          with(same(errors)));
      will(throwException(new PassphraseException()));
    } });
    
    bean.setRequest(request);
    bean.getFileUploadEditor().setFile0(file);
    assertThat(bean.prepare(), is(nullValue()));
  }


  @SuppressWarnings("unchecked")
  private Expectations prepareImportExpectations(final Part file,
      final Action preparationOutcome) 
      throws Exception {
    return new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          with(same(request)),
          (List<FileContentModel>) with(not(empty())),
          with(same(bean.getPasswordEditor())),
          with(same(errors))); 
      will(preparationOutcome);
    } };
  }
    
  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(endConversationExpectations());
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(same(errors)));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), equalTo(ImportCredentialBean.SUCCESS_OUTCOME_ID));    
  }

  @Test
  public void testSaveImportError() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), nullValue());    
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(endConversationExpectations());    
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
    bean.setDetails(details);
    bean.setCredential(credential);
    assertThat(bean.protect(), is(nullValue()));        
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
