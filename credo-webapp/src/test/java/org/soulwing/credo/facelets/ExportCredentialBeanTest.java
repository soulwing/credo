/*
 * File created on Feb 24, 2014 
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.context.Conversation;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.AccessDeniedException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.ExportService;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;

/**
 * Unit tests for {@link ExportCredentialBean}.
 *
 * @author Carl Harris
 */
public class ExportCredentialBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private ExportService exportService;
  
  @Mock
  private Errors errors;
  
  @Mock
  private ExportRequest request;
  
  @Mock
  private ExportPreparation preparation;
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private ExternalContext externalContext;
  
  private ExportCredentialBean bean = new ExportCredentialBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.exportService = exportService;
    bean.facesContext = facesContext;
    bean.errors = errors;
  }
  
  @Test
  public void testInit() throws Exception {
    final String loginName = "someUser";
    context.checking(new Expectations() { { 
      oneOf(facesContext).getExternalContext();
      will(returnValue(externalContext));
      oneOf(externalContext).getRemoteUser();
      will(returnValue(loginName));
    } });
    
    bean.init();
    assertThat(bean.getProtection().getLoginName(), is(equalTo(loginName)));
  }

  @Test
  public void testCreateExportRequest() throws Exception {
    final Long id = -1L;
    final Credential credential = context.mock(Credential.class);
    final UserGroup group = context.mock(UserGroup.class);
    final String groupName = "someGroup";
    context.checking(new Expectations() { { 
      oneOf(exportService).newExportRequest(with(id));
      will(returnValue(request));
      oneOf(request).getCredential();
      will(returnValue(credential));
      oneOf(credential).getOwner();
      will(returnValue(group));
      oneOf(group).getName();
      will(returnValue(groupName));
      oneOf(request).setProtectionParameters(with(same(bean.getProtection())));
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } });
    
    bean.setId(id);
    assertThat(bean.createExportRequest(), is(nullValue()));
    assertThat(bean.getExportRequest(), is(sameInstance(request)));
  }

  @Test
  public void testCreateExportRequestNotFound() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { { 
      oneOf(exportService).newExportRequest(with(id));
      will(throwException(new NoSuchCredentialException()));
      oneOf(errors).addError(with(containsString("NotFound")),
          (Object[]) with(arrayContaining(id)));
    } });
    
    bean.setId(id);
    assertThat(bean.createExportRequest(), is(nullValue()));    
  }
  
  @Test
  public void testPrepareDownload() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)));
      will(returnValue(preparation));
    } });
    
    bean.setExportRequest(request);
    assertThat(bean.prepareDownload(), 
        is(equalTo(ExportCredentialBean.PREPARED_OUTCOME_ID)));
    assertThat(bean.getExportPreparation(), is(sameInstance(preparation)));
  }

  @Test
  public void testPrepareDownloadIncorrectPassphrase() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)));
      will(throwException(new PassphraseException()));
      oneOf(errors).addError(with(equalTo("passphrase")), 
          with(containsString("Incorrect")),
          with(emptyArray()));
    } });
    
    bean.setExportRequest(request);
    assertThat(bean.prepareDownload(), is(nullValue()));
  }

  @Test(expected = RuntimeException.class)
  public void testPrepareDownloadAccessDenied() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)));
      will(throwException(new AccessDeniedException()));
    } });
    
    bean.setExportRequest(request);
    bean.prepareDownload();
  }


  @Test
  public void testPrepareDownloadError() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)));
      will(throwException(new ExportException()));
    } });
    
    bean.setExportRequest(request);
    assertThat(bean.prepareDownload(), 
        is(equalTo(ExportCredentialBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testDownload() throws Exception {
    final String encoding = null;
    final OutputStream outputStream = new ByteArrayOutputStream();
    
    context.checking(newDownloadExpectations(encoding, outputStream));
    context.checking(new Expectations() { { 
      oneOf(preparation).writeContent(with(same(outputStream)));
      oneOf(facesContext).responseComplete();
      oneOf(conversation).setTimeout(with(any(Long.class)));
    } });
    
    bean.setExportPreparation(preparation);
    bean.download();
  }

  @Test
  public void testDownloadText() throws Exception {
    final String encoding = "someEncoding";
    final OutputStream outputStream = new ByteArrayOutputStream();
    
    context.checking(newDownloadExpectations(encoding, outputStream));
    context.checking(new Expectations() { { 
      oneOf(externalContext).setResponseCharacterEncoding(with(encoding));
      oneOf(preparation).writeContent(with(same(outputStream)));
      oneOf(facesContext).responseComplete();
      oneOf(conversation).setTimeout(with(any(Long.class)));
    } });
       
    bean.setExportPreparation(preparation);
    bean.download();
  }

  @Test(expected = RuntimeException.class)
  public void testDownloadError() throws Exception {
    final String encoding = null;
    final OutputStream outputStream = new ByteArrayOutputStream();
    
    context.checking(newDownloadExpectations(encoding, outputStream));
    context.checking(new Expectations() { { 
      oneOf(preparation).writeContent(with(same(outputStream)));
      will(throwException(new IOException()));
      oneOf(conversation).end();
    } });
       
    bean.setExportPreparation(preparation);
    bean.download();
  }

  private Expectations newDownloadExpectations(final String encoding,
      final OutputStream outputStream) throws IOException {
    final String contentType = "someContentType";
    final String fileName = "someFilename";
    return new Expectations() { { 
      oneOf(facesContext).getExternalContext();
      will(returnValue(externalContext));
      oneOf(preparation).getContentType();
      will(returnValue(contentType));
      oneOf(preparation).getCharacterEncoding();
      will(returnValue(encoding));
      oneOf(preparation).getFileName();
      will(returnValue(fileName));
      oneOf(externalContext).setResponseContentType(
          with(same(contentType)));
      oneOf(externalContext).setResponseHeader(
          with(ExportCredentialBean.CONTENT_DISPOSITION_HEADER),
          with(containsString(fileName)));
      oneOf(externalContext).getResponseOutputStream();
      will(returnValue(outputStream));
    } };
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } });
    
    assertThat(bean.cancel(), 
        is(equalTo(ExportCredentialBean.CANCEL_OUTCOME_ID)));
  }

}
