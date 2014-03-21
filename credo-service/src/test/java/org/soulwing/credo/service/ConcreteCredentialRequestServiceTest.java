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
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificationRequest;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Tag;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.request.CredentialRequestEditorFactory;
import org.soulwing.credo.service.request.CredentialRequestGenerator;

/**
 * Unit tests for {@link ConcreteCredentialRequestService}.
 *
 * @author Carl Harris
 */
public class ConcreteCredentialRequestServiceTest {

  private static final String REQUEST_NAME = "requestName";

  private static final String CSR_CONTENT = "csrContent";

  private static final long CREDENTIAL_ID = -1L;

  private static final String NOTE = "note";
   
  private static final String[] TAGS = new String[0];
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private CredentialRequestEditorFactory editorFactory;
  
  @Mock
  private CredentialRequestGenerator generator;
  
  @Mock
  private CredentialRequestRepository requestRepository;
  
  @Mock
  private TagService tagService;
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRequestEditor editor;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private CredentialCertificationRequest certificationRequest;
  
  @Mock
  private FileDownloadResponse response;
  
  @Mock
  private ProtectionParameters protection;
  
  @Mock
  private Errors errors;
  
  @Mock
  private Set<? extends Tag> tags;
  
  private ConcreteCredentialRequestService service =
      new ConcreteCredentialRequestService();
  
  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.editorFactory = editorFactory;
    service.generator = generator;
    service.requestRespository = requestRepository;
    service.tagService = tagService;
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
    service.createRequest(editor, protection, errors);
  }
  
  @Test(expected = GroupAccessException.class)
  public void testCreateSigningRequestWhenGroupAccessDenied() throws Exception {
    context.checking(generateExpectations(
        throwException(new GroupAccessException("some message"))));
    service.createRequest(editor, protection, errors);
  }

  @Test(expected = CredentialRequestException.class)
  public void testCreateSigningRequestFailure() throws Exception {
    context.checking(generateExpectations(
        throwException(new CredentialRequestException())));
    service.createRequest(editor, protection, errors);
  }

  @Test
  public void testCreateSigningRequestSuccess() throws Exception {
    context.checking(generateExpectations(returnValue(request)));
    context.checking(requestExpectations());
    assertThat(service.createRequest(editor, protection, errors),
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
  
  private Expectations requestExpectations() {
    return new Expectations() { {
      oneOf(editor).getName();
      will(returnValue(REQUEST_NAME));
      oneOf(request).setName(with(REQUEST_NAME));
      oneOf(editor).getNote();
      will(returnValue(NOTE));
      oneOf(request).setNote(with(NOTE));
      oneOf(editor).getTags();
      will(returnValue(TAGS));
      oneOf(request).setTags(tags);
      oneOf(tagService).resolve(TAGS);
      will(returnValue(tags));
    } };
  }
  
  @Test
  public void testSaveSigningRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).add(with(same(request)));
    } });
    
    service.saveRequest(request);
  }
  
  @Test
  public void testDownloadSigningRequest() throws Exception {
    final StringWriter writer = new StringWriter();
    context.checking(new Expectations() { { 
      oneOf(request).getCertificationRequest();
      will(returnValue(certificationRequest));
      oneOf(certificationRequest).getContent();
      will(returnValue(CSR_CONTENT));
      oneOf(request).getName();
      will(returnValue(REQUEST_NAME));
      oneOf(response).setFileName(with(containsString(REQUEST_NAME)));
      oneOf(response).setContentType(
          with(ConcreteCredentialRequestService.CONTENT_TYPE));
      oneOf(response).setCharacterEncoding(
          with(ConcreteCredentialRequestService.CHARACTER_ENCODING));
      oneOf(response).getWriter();
      will(returnValue(writer));
    } });
    
    service.downloadRequest(request, response);
    assertThat(writer.toString(), is(equalTo(CSR_CONTENT)));
  }

}
