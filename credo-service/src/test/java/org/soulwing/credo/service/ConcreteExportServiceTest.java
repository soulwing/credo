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
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.CredentialExporter;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.protect.GroupAccessException;
import org.soulwing.credo.service.protect.UserAccessException;

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
  private Instance<CredentialExporter> exporters;

  @Mock
  private CredentialExporter exporter;

  @Mock
  private ExportRequest request;
  
  @Mock
  private Credential credential;

  @Mock
  private PrivateKeyWrapper credentialPrivateKey;
  
  @Mock
  private ExportPreparation preparation;
  
  @Mock
  private ProtectionParameters protection;

  @Mock
  private CredentialProtectionService protectionService;
  
  private ConcreteExportService exportService = new ConcreteExportService();
  
  @Before
  public void setUp() throws Exception {
    exportService.credentialService = credentialService;
    exportService.exporters = exporters;
    exportService.protectionService = protectionService;
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
    context.checking(unprotectCredentialExpectations(
        returnValue(credentialPrivateKey)));
    context.checking(findExporterExpectations(returnValue(exporters)));
    context.checking(new Expectations() { { 
      oneOf(exporter).exportCredential(with(same(request)), 
          with(same(credentialPrivateKey)));
      will(returnValue(preparation));
    } });
    assertThat(exportService.prepareExport(request), 
        sameInstance(preparation));
  }

  @Test(expected = RuntimeException.class)
  public void testPrepareExportIOException() throws Exception {
    context.checking(unprotectCredentialExpectations(
        returnValue(credentialPrivateKey)));
    context.checking(findExporterExpectations(returnValue(exporters)));
    context.checking(new Expectations() { { 
      oneOf(exporter).exportCredential(with(same(request)), 
          with(credentialPrivateKey));
      will(throwException(new IOException()));
    } });
    
    exportService.prepareExport(request); 
  }
  
  @Test(expected = PassphraseException.class)
  public void testPrepareExportWhenIncorrectPassword() throws Exception {
    context.checking(unprotectCredentialExpectations(
        throwException(new UserAccessException(new Exception()))));
    exportService.prepareExport(request);
  }

  @Test(expected = AccessDeniedException.class)
  public void testPrepareExportWhenNotUserGroupMember() throws Exception {
    context.checking(unprotectCredentialExpectations(
        throwException(new GroupAccessException("some message"))));
    exportService.prepareExport(request);
  }

  @SuppressWarnings("unchecked")
  private Expectations findExporterExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(request).getFormat();
      will(returnValue("format"));
      oneOf(exporters).select(with(arrayContaining(any(Annotation.class))));
      will(outcome);
      allowing(exporters).get();
      will(returnValue(exporter));
    } };
  }
  
  private Expectations unprotectCredentialExpectations(final Action outcome)
      throws Exception {
    final UserGroup group = context.mock(UserGroup.class);
    return new Expectations() { { 
      oneOf(request).getCredential();
      will(returnValue(credential));
      oneOf(request).getProtectionParameters();
      will(returnValue(protection));
      oneOf(credential).getOwner();
      will(returnValue(group));
      oneOf(group).getName();
      will(returnValue("someGroup"));
      oneOf(protectionService).unprotect(with(credential), 
          (ProtectionParameters) with(hasProperty("groupName", equalTo("someGroup"))));
      will(outcome);
    } };
  }

}
