/*
 * File created on Apr 12, 2014 
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
package org.soulwing.credo.service.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.StringWriter;
import java.io.Writer;
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
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.TagService;
import org.soulwing.credo.service.UserAccessException;

/**
 * Unit tests for {@link CreateCredentialRequestServiceBean}.
 * 
 * @author Carl Harris
 */
public class CreateCredentialRequestServiceBeanTest {

  private static final long REQUEST_ID = -1L;

  private static final String LOGIN_NAME = "loginName";

  private static final String GROUP_NAME = "groupName";

  private static final String REQUEST_NAME = "requestName";

  private static final String CSR_CONTENT = "csrContent";

  private static final long CREDENTIAL_ID = REQUEST_ID;

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

  private CreateCredentialRequestServiceBean service = new CreateCredentialRequestServiceBean();

  @Before
  public void setUp() throws Exception {
    service.credentialRepository = credentialRepository;
    service.editorFactory = editorFactory;
    service.generator = generator;
    service.requestRepository = requestRepository;
    service.tagService = tagService;
  }

  @Test
  public void testCreateEditorForCredential() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
        will(returnValue(credential));
        oneOf(editorFactory).newEditor(with(same(credential)));
        will(returnValue(editor));
      }
    });

    assertThat(service.createEditor(CREDENTIAL_ID, errors),
        is(sameInstance(editor)));
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testCreateEditorWhenCredentialNotFound() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
        will(returnValue(null));
        oneOf(errors).addError(with("credentialId"),
            with("credentialNotFound"),
            (Object[]) with(arrayContaining(CREDENTIAL_ID)));
      }
    });

    service.createEditor(CREDENTIAL_ID, errors);
  }

  @Test(expected = PassphraseException.class)
  public void testCreateRequestWhenPassphraseIncorrect() throws Exception {
    context
        .checking(generateExpectations(throwException(new UserAccessException(
            new Exception()))));
    context.checking(new Expectations() {
      {
        oneOf(errors).addError(with("password"), with("passwordIncorrect"),
            with(emptyArray()));
      }
    });
    service.createRequest(editor, protection, errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testCreateRequestWhenGroupAccessDenied() throws Exception {
    context
        .checking(generateExpectations(throwException(new GroupAccessException(
            GROUP_NAME))));
    context.checking(new Expectations() {
      {
        allowing(protection).getGroupName();
        will(returnValue(GROUP_NAME));
        oneOf(errors).addError(with("owner"), with("groupAccessDenied"),
            (Object[]) with(arrayContaining(GROUP_NAME)));
      }
    });
    service.createRequest(editor, protection, errors);
  }

  @Test(expected = NoSuchGroupException.class)
  public void testCreateRequestWhenNoSuchGroup() throws Exception {
    context
        .checking(generateExpectations(throwException(new NoSuchGroupException())));
    context.checking(new Expectations() {
      {
        allowing(protection).getGroupName();
        will(returnValue(GROUP_NAME));
        oneOf(errors).addError(with("owner"),
            with("credentialOwnerNotFound"),
            (Object[]) with(arrayContaining(GROUP_NAME)));
      }
    });
    service.createRequest(editor, protection, errors);
  }

  @Test(expected = CredentialRequestException.class)
  public void testCreateRequestFailure() throws Exception {
    context
        .checking(generateExpectations(throwException(new CredentialRequestException())));
    service.createRequest(editor, protection, errors);
  }

  @Test
  public void testCreateRequestForNewCredential() throws Exception {
    context.checking(generateExpectations(returnValue(request)));
    context.checking(newCredentialExpectations());
    context.checking(requestExpectations());
    assertThat(service.createRequest(editor, protection, errors),
        is(sameInstance(request)));
  }

  @Test
  public void testCreateRequestForExistingCredentialWhenFound()
      throws Exception {
    context.checking(generateExpectations(returnValue(request)));
    context.checking(existingCredentialExpectations(credential));
    context.checking(requestExpectations());
    assertThat(service.createRequest(editor, protection, errors),
        is(sameInstance(request)));
  }

  @Test
  public void testCreateRequestForExistingCredentialWhenNotFound()
      throws Exception {
    context.checking(generateExpectations(returnValue(request)));
    context.checking(existingCredentialExpectations(null));
    context.checking(requestExpectations());
    assertThat(service.createRequest(editor, protection, errors),
        is(sameInstance(request)));
  }

  private Expectations generateExpectations(final Action outcome)
      throws Exception {
    return new Expectations() {
      {
        oneOf(generator).generate(with(same(editor)), with(same(protection)));
        will(outcome);
      }
    };
  }

  private Expectations requestExpectations() {
    return new Expectations() {
      {
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
      }
    };
  }

  private Expectations newCredentialExpectations() {
    return new Expectations() {
      {
        oneOf(editor).getCredentialId();
        will(returnValue(null));
      }
    };
  }

  private Expectations existingCredentialExpectations(
      final Credential retValue) {
    return new Expectations() {
      {
        allowing(editor).getCredentialId();
        will(returnValue(CREDENTIAL_ID));
        oneOf(credentialRepository).findById(with(CREDENTIAL_ID));
        will(returnValue(retValue));
        oneOf(request).setCredential(with(retValue));
      }
    };
  }

  @Test
  public void testSaveRequest() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(requestRepository).add(with(same(request)));
      }
    });

    service.saveRequest(request, errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testSaveRequestWhenOwnerAccessDenied() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(requestRepository).add(with(same(request)));
        will(throwException(new OwnerAccessControlException(GROUP_NAME,
            LOGIN_NAME)));
        oneOf(errors).addError(with("owner"), with("groupAccessDenied"),
            (Object[]) with(arrayContaining(GROUP_NAME)));
      }
    });

    service.saveRequest(request, errors);
  }

  @Test
  public void testDownloadRequest() throws Exception {
    final StringWriter writer = new StringWriter();
    context.checking(downloadExpectations(writer));
    service.downloadRequest(request, response);
    assertThat(writer.toString(), is(equalTo(CSR_CONTENT)));
  }

  private Expectations downloadExpectations(final Writer writer)
      throws Exception {
    return new Expectations() {
      {
        oneOf(request).getCertificationRequest();
        will(returnValue(certificationRequest));
        oneOf(certificationRequest).getContent();
        will(returnValue(CSR_CONTENT));
        oneOf(request).getName();
        will(returnValue(REQUEST_NAME));
        oneOf(response).setFileName(with(containsString(REQUEST_NAME)));
        oneOf(response).setContentType(
            with(CreateCredentialRequestServiceBean.CONTENT_TYPE));
        oneOf(response).setCharacterEncoding(
            with(CreateCredentialRequestServiceBean.CHARACTER_ENCODING));
        oneOf(response).getWriter();
        will(returnValue(writer));
      }
    };
  }

}
