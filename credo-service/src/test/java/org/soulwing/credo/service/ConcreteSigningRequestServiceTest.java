/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.StringWriter;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.SigningRequest;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.SigningRequestRepository;
import org.soulwing.credo.service.request.SigningRequestEditorFactory;
import org.soulwing.credo.service.request.SigningRequestGenerator;

/**
 * Unit tests for {@link ConcreteSigningRequestService}.
 *
 * @author Carl Harris
 */
public class ConcreteSigningRequestServiceTest {

  private static final String REQUEST_NAME = "requestName";

  private static final String CSR_CONTENT = "csrContent";

  private static final long CREDENTIAL_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private SigningRequestEditorFactory editorFactory;
  
  @Mock
  private SigningRequestGenerator generator;
  
  @Mock
  private SigningRequestRepository requestRepository;
  
  @Mock
  private Credential credential;
  
  @Mock
  private SigningRequestEditor editor;
  
  @Mock
  private SigningRequest request;
  
  @Mock
  private FileDownloadResponse response;
  
  @Mock
  private ProtectionParameters protection;
  
  @Mock
  private Errors errors;
  
  private ConcreteSigningRequestService service =
      new ConcreteSigningRequestService();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.editorFactory = editorFactory;
    service.generator = generator;
    service.requestRespository = requestRepository;
  }
  
  @Test
  public void testCreateEditorForCredential() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
      will(returnValue(credential));
      oneOf(editorFactory).newEditor(with(same(credential)));
      will(returnValue(editor));
    } } );
    
    assertThat(service.createEditor(CREDENTIAL_ID, errors),
        is(sameInstance(editor)));
  }
  
  @Test(expected = NoSuchCredentialException.class)
  public void testCreateEditorWhenCredentialNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
      will(returnValue(null));
      oneOf(errors).addError(
          with("credentialId"), 
          with("credentialNotFound"), 
          (Object[]) with(arrayContaining(CREDENTIAL_ID)));
    } } );
    
    service.createEditor(CREDENTIAL_ID, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testCreateSigningRequestWhenPassphraseIncorrect() 
      throws Exception {
    context.checking(generateExpectations(
        throwException(new UserAccessException(new Exception()))));
    service.createSigningRequest(editor, protection, errors);
  }
  
  @Test(expected = GroupAccessException.class)
  public void testCreateSigningRequestWhenGroupAccessDenied() throws Exception {
    context.checking(generateExpectations(
        throwException(new GroupAccessException("some message"))));
    service.createSigningRequest(editor, protection, errors);
  }

  @Test(expected = SigningRequestException.class)
  public void testCreateSigningRequestFailure() throws Exception {
    context.checking(generateExpectations(
        throwException(new SigningRequestException())));
    service.createSigningRequest(editor, protection, errors);
  }

  @Test
  public void testCreateSigningRequestSuccess() throws Exception {
    context.checking(generateExpectations(returnValue(request)));
    assertThat(service.createSigningRequest(editor, protection, errors),
        is(sameInstance(request)));
  }
  
  private Expectations generateExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(generator).generate(with(same(editor)), with(same(protection)), 
          with(same(errors)));
      will(outcome);
    } };
  }
  
  @Test
  public void testSaveSigningRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).add(with(same(request)));
    } });
    
    service.saveSigningRequest(request);
  }
  
  @Test
  public void testDownloadSigningRequest() throws Exception {
    final StringWriter writer = new StringWriter();
    context.checking(new Expectations() { { 
      oneOf(request).getContent();
      will(returnValue(CSR_CONTENT));
      oneOf(request).getName();
      will(returnValue(REQUEST_NAME));
      oneOf(response).setFileName(with(containsString(REQUEST_NAME)));
      oneOf(response).setContentType(
          with(ConcreteSigningRequestService.CONTENT_TYPE));
      oneOf(response).setCharacterEncoding(
          with(ConcreteSigningRequestService.CHARACTER_ENCODING));
      oneOf(response).getWriter();
      will(returnValue(writer));
    } });
    
    service.downloadSigningRequest(request, response);
    assertThat(writer.toString(), is(equalTo(CSR_CONTENT)));
  }

}
