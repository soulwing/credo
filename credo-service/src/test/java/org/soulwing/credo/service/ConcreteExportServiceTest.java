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
package org.soulwing.credo.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;
import java.util.Iterator;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.service.exporter.CredentialExportProvider;
import org.soulwing.credo.service.exporter.CredentialExporter;

/**
 * Unit tests for {@link ConcreteExportService}.
 *
 * @author Carl Harris
 */
public class ConcreteExportServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialService credentialService;
  
  @Mock
  private Credential credential;
  
  @Mock
  private Instance<CredentialExportProvider> exportProvider;
  
  @Mock
  private Iterator<CredentialExportProvider> providerIterator;
  
  @Mock
  private CredentialExportProvider provider;
  
  @Mock
  private CredentialExporter exporter;

  @Mock
  private ExportRequest request;
  
  @Mock
  private ExportPreparation preparation;
  
  private ConcreteExportService exportService = new ConcreteExportService();
  
  @Before
  public void setUp() throws Exception {
    exportService.credentialService = credentialService;
    exportService.exportProvider = exportProvider;
  }
  
  @Test
  public void testCreateExportRequest() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { {
      oneOf(credentialService).findCredentialById(with(same(id)));
      will(returnValue(credential));
    } });
    
    ExportRequest request = exportService.newExportRequest(id);
    assertThat(request, is(not(nullValue())));
    assertThat(request, hasProperty("credential", sameInstance(credential)));
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testCreateExportRequestNotFound() throws Exception {
    final Long id = -1L;
    context.checking(new Expectations() { {
      oneOf(credentialService).findCredentialById(with(same(id)));
      will(throwException(new NoSuchCredentialException()));
    } });
    
    exportService.newExportRequest(id);
  }

  @Test
  public void testPrepareExport() throws Exception {
    context.checking(prepareExportExpectations(true));
    context.checking(new Expectations() { { 
      oneOf(provider).newExporter();
      will(returnValue(exporter));
      oneOf(exporter).exportCredential(with(same(request)));
      will(returnValue(preparation));
    } });
    assertThat(exportService.prepareExport(request), 
        sameInstance(preparation));
  }

  @Test(expected = RuntimeException.class)
  public void testPrepareExportIOException() throws Exception {
    context.checking(prepareExportExpectations(true));
    context.checking(new Expectations() { { 
      oneOf(provider).newExporter();
      will(returnValue(exporter));
      oneOf(exporter).exportCredential(with(same(request)));
      will(throwException(new IOException()));
    } });
    
    exportService.prepareExport(request); 
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrepareExportUnsupportedFormat() throws Exception {
    context.checking(prepareExportExpectations(false));
    exportService.prepareExport(request); 
  }


  private Expectations prepareExportExpectations(final boolean supports)
      throws IOException, PassphraseException, ExportException {
    final ExportFormat format = ExportFormat.PEM_ARCHIVE;
    return new Expectations() { {
      oneOf(exportProvider).iterator();
      will(returnValue(providerIterator));
      atLeast(1).of(providerIterator).hasNext();
      will(onConsecutiveCalls(returnValue(true), returnValue(false)));
      oneOf(providerIterator).next();
      will(returnValue(provider));
      allowing(request).getFormat();
      will(returnValue(format));
      oneOf(provider).supports(with(format));
      will(returnValue(supports));
    } };
  }
}
