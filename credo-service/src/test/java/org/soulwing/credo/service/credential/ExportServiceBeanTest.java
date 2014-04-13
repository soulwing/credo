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
package org.soulwing.credo.service.credential;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.credential.ExportServiceBean;
import org.soulwing.credo.service.credential.CredentialService;
import org.soulwing.credo.service.credential.ExportPreparation;
import org.soulwing.credo.service.credential.ExportRequest;
import org.soulwing.credo.service.crypto.PasswordGenerator;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.CredentialExporter;
import org.soulwing.credo.service.exporter.CredentialExporterRegistry;
import org.soulwing.credo.service.protect.CredentialProtectionService;

/**
 * Unit tests for {@link ExportServiceBean}.
 *
 * @author Carl Harris
 */
public class ExportServiceBeanTest {

  private static final String GROUP_NAME = "someGroup";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialService credentialService;
  
  @Mock
  private CredentialExporterRegistry exporterRegistry;
  
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
  
  @Mock
  private PasswordGenerator passwordGenerator;
  
  @Mock
  private Errors errors;
  
  private ExportServiceBean exportService = new ExportServiceBean();
  
  @Before
  public void setUp() throws Exception {
    exportService.credentialService = credentialService;
    exportService.exporterRegistry = exporterRegistry;
    exportService.protectionService = protectionService;
    exportService.passwordGenerator = passwordGenerator;
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
    context.checking(findExporterExpectations(returnValue(exporter)));
    context.checking(new Expectations() { { 
      oneOf(exporter).exportCredential(with(same(request)), 
          with(same(credentialPrivateKey)));
      will(returnValue(preparation));
    } });
    assertThat(exportService.prepareExport(request, errors), 
        sameInstance(preparation));
  }

  @Test(expected = RuntimeException.class)
  public void testPrepareExportIOException() throws Exception {
    context.checking(unprotectCredentialExpectations(
        returnValue(credentialPrivateKey)));
    context.checking(findExporterExpectations(returnValue(exporter)));
    context.checking(new Expectations() { { 
      oneOf(exporter).exportCredential(with(same(request)), 
          with(credentialPrivateKey));
      will(throwException(new IOException()));
    } });
    
    exportService.prepareExport(request, errors); 
  }
  
  @Test(expected = PassphraseException.class)
  public void testPrepareExportWhenIncorrectPassword() throws Exception {
    context.checking(unprotectCredentialExpectations(
        throwException(new UserAccessException(new Exception()))));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with(equalTo("password")), 
          with(containsString("Incorrect")),
          with(emptyArray()));
    } });
    exportService.prepareExport(request, errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testPrepareExportWhenNotUserGroupMember() throws Exception {
    context.checking(unprotectCredentialExpectations(
        throwException(new GroupAccessException(GROUP_NAME))));
    context.checking(new Expectations() { { 
      oneOf(errors).addError(with(equalTo("groupAccessDenied")), 
          (Object[]) with(arrayContaining(GROUP_NAME)));
    } });
    exportService.prepareExport(request, errors);
  }

  @Test
  public void testGeneratePassword() throws Exception {
    final Password password = Password.EMPTY;
    
    context.checking(new Expectations() { { 
      oneOf(passwordGenerator).generatePassword();
      will(returnValue(password));
    } });
    
    assertThat(exportService.generatePassphrase(), is(sameInstance(password)));
  }
  
  private Expectations findExporterExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(exporterRegistry).findExporter(request);
      will(outcome);
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
      will(returnValue(GROUP_NAME));
      oneOf(protectionService).unprotect(with(credential), 
          (ProtectionParameters) with(hasProperty("groupName", equalTo(GROUP_NAME))));
      will(outcome);
    } };
  }

}
