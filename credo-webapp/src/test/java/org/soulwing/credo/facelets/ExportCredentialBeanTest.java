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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import javax.enterprise.context.Conversation;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportFormat;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.ExportService;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;

/**
 * Unit tests for {@link ExportCredentialBean}.
 *
 * @author Carl Harris
 */
public class ExportCredentialBeanTest {

  private static final String GROUP_NAME = "someGroup";

  private static final String SUFFIX = ".suffix";

  private static final String FILE_NAME = "fileName";

  private static final String VARIANT_ID = "variantId";

  private static final String FORMAT_ID = "formatId";

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
  
  @Mock
  private PartialViewContext partialViewContext;
  
  @Mock
  private UIViewRoot viewRoot;
  
  @Mock
  private ExportFormat format;
  
  @Mock
  private ExportFormat.Variant variant;
  
  private ExportCredentialBean bean = new ExportCredentialBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.exportService = exportService;
    bean.facesContext = facesContext;
    bean.errors = errors;
    bean.passwordEditor = new PasswordFormEditor();
  }
  
  @Test
  public void testCreateExportRequest() throws Exception {
    final Long id = -1L;
    final Credential credential = context.mock(Credential.class);
    final UserGroup group = context.mock(UserGroup.class);
    context.checking(new Expectations() { { 
      oneOf(exportService).newExportRequest(with(id));
      will(returnValue(request));
      oneOf(request).getCredential();
      will(returnValue(credential));
      oneOf(credential).getOwner();
      will(returnValue(group));
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(request).setProtectionParameters(with(same(bean.getPasswordEditor())));
      oneOf(exportService).getDefaultFormat();
      will(returnValue(format));
      oneOf(format).getId();
      will(returnValue(FORMAT_ID));
      oneOf(request).setFormat(FORMAT_ID);
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } });
    context.checking(newFormatSelectedExpectations());
    context.checking(newVariantSelectedExpectations());
    context.checking(resetRenderedInputsExpectations());
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
      oneOf(exportService).prepareExport(with(same(request)), 
          with(same(errors)));
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
      oneOf(exportService).prepareExport(with(same(request)), 
          with(same(errors)));
      will(throwException(new PassphraseException()));
    } });
        bean.setExportRequest(request);
    assertThat(bean.prepareDownload(), is(nullValue()));
  }

  @Test
  public void testPrepareDownloadAccessDenied() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)), 
          with(same(errors)));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    bean.setExportRequest(request);
    assertThat(bean.prepareDownload(), 
        is(equalTo(ExportCredentialBean.FAILURE_OUTCOME_ID)));
  }

  @Test
  public void testPrepareDownloadError() throws Exception {
    context.checking(new Expectations() { {
      oneOf(exportService).prepareExport(with(same(request)), 
          with(same(errors)));
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

  @Test
  public void testFormatSelected() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(request).setFormat(with(FORMAT_ID));
    } });
    context.checking(newFormatSelectedExpectations());
    context.checking(newVariantSelectedExpectations());
    context.checking(resetRenderedInputsExpectations());
    bean.setExportRequest(request);
    bean.setFormat(FORMAT_ID);
    bean.formatSelected(context.mock(AjaxBehaviorEvent.class));
  }

  @Test
  public void testVariantSelected() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(request).setVariant(with(VARIANT_ID));
    } });
    context.checking(newVariantSelectedExpectations());
    context.checking(resetRenderedInputsExpectations());
    bean.setSelectedFormat(format);
    bean.setExportRequest(request);
    bean.setVariant(VARIANT_ID);
    bean.variantSelected(context.mock(AjaxBehaviorEvent.class));
  }

  private Expectations newFormatSelectedExpectations() {
    return new Expectations() { { 
      oneOf(request).getFormat();
      will(returnValue(FORMAT_ID));
      oneOf(exportService).findFormat(with(FORMAT_ID));
      will(returnValue(format));      
      oneOf(format).getDefaultVariant();
      will(returnValue(variant));
      oneOf(variant).getId();
      will(returnValue(VARIANT_ID));
      oneOf(request).setVariant(VARIANT_ID);
      
    } };
  }
  
  private Expectations newVariantSelectedExpectations() {
    return new Expectations() { { 
      oneOf(request).getVariant();
      will(returnValue(VARIANT_ID));
      oneOf(format).findVariant(with(VARIANT_ID));
      will(returnValue(variant));
      oneOf(request).getFileName();
      will(returnValue(FILE_NAME));
      oneOf(variant).getSuffix();
      will(returnValue(SUFFIX));
      oneOf(request).setFileName(with(FILE_NAME + SUFFIX));      
    } };
  }
  
  private Expectations resetRenderedInputsExpectations() {
    return new Expectations() { { 
      allowing(facesContext).getPartialViewContext();
      will(returnValue(partialViewContext));
      allowing(partialViewContext).getRenderIds();
      will(returnValue(Collections.emptySet()));
      allowing(facesContext).getViewRoot();
      will(returnValue(viewRoot));
    } };
  }
  
  @Test
  public void testGenerateExportPassphrase() throws Exception {
    final Password passphrase = new Password(new char[0]);
    context.checking(new Expectations() { { 
      oneOf(exportService).generatePassphrase();
      will(returnValue(passphrase));
      oneOf(request).setExportPassphrase(with(same(passphrase)));
    } });
  
    bean.setExportRequest(request);
    bean.generateExportPassphrase();
    assertThat(bean.getExportPassphraseAgain(), is(sameInstance(passphrase)));
  }
  
}
