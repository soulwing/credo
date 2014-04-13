/*
 * File created on Feb 19, 2014 
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
package org.soulwing.credo.service.importer;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.NoContentException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.TimeOfDayService;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.CredentialBag;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * Unit tests for {@link CredentialImporterBean}.
 *
 * @author Carl Harris
 */
public class CredentialImporterBeanTest {

  private static final Password PASSPHRASE = new Password(new char[] { 'a' });

  private static final Password EMPTY_PASSPHRASE = Password.EMPTY;

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialBag bag;
 
  @Mock
  private TimeOfDayService timeOfDayService;
  
  @Mock
  private Errors errors;
  
  @Mock
  private PrivateKeyWrapper privateKey;

  @Mock
  private CertificateWrapper certificate;

  private CredentialImporterBean importer = new CredentialImporterBean();
  
  @Before
  public void setUp() throws Exception {
    importer.bag = bag;
    importer.timeOfDayService = timeOfDayService;
  }
  
  @Test
  public void testLoadFile() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(returnValue(1));
    } });
    
    importer.loadFile(inputStream);    
  }
  
  @Test(expected = IOException.class)
  public void testLoadFileThrowsIOException() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(throwException(new IOException()));
    } });
    
    importer.loadFile(inputStream);
  }

  @Test(expected = NoContentException.class)
  public void testLoadFileWithNoContent() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(returnValue(0));
    } });
    
    importer.loadFile(inputStream);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWithNoPrivateKey() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(bag).findPrivateKey();
      will(returnValue(null));
      oneOf(errors).addError(with(containsString("NoPrivateKey")),
          with(emptyArray()));
    } });
    
    importer.validateAndImport(EMPTY_PASSPHRASE, errors);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWithMultiplePrivateKeys() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(privateKey)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      oneOf(errors).addError(with(containsString("Multiple")),
          with(emptyArray()));
    } });
    
    importer.validateAndImport(EMPTY_PASSPHRASE, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testVerifyWithEmptyPassphrase() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      oneOf(privateKey).setProtectionParameter(with(same(EMPTY_PASSPHRASE)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(throwException(new IncorrectPassphraseException()));
    } });
    
    importer.validateAndImport(EMPTY_PASSPHRASE, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testVerifyWithIncorrectPassphrase() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      oneOf(privateKey).setProtectionParameter(with(same(PASSPHRASE)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(throwException(new IncorrectPassphraseException()));
      oneOf(errors).addError(with("password"), 
          with(containsString("Incorrect")), 
          with(emptyArray()));
    } });
    
    importer.validateAndImport(PASSPHRASE, errors);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWhenSubjectCertificateNotFound() throws Exception {
    context.checking(privateKeyExpectations());
    context.checking(new Expectations() { { 
      allowing(privateKey).setProtectionParameter(PASSPHRASE);
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(returnValue(null));
      oneOf(errors).addError(with(containsString("NoSubject")), 
          with(emptyArray()));
    } });

    importer.validateAndImport(PASSPHRASE, errors);
  }

  @Test
  public void testVerifyWhenSubjectCertificateExpired() throws Exception {
    context.checking(privateKeyExpectations());
    context.checking(certificateExpectations());
    context.checking(expirationExpectations(new Date(0), new Date(1)));
    context.checking(new Expectations() { { 
      oneOf(errors).addWarning(with(containsString("Expired")), 
          with(emptyArray()));
      allowing(bag).findAuthorityCertificates(with(same(certificate)));
      will(returnValue(Collections.emptyList()));
      allowing(errors).addWarning(with(containsString("Incomplete")),
          with(emptyArray()));
    } });

    importer.validateAndImport(PASSPHRASE, errors);
  }
  

  @Test
  public void testVerifyWhenAuthorityChainEmpty() throws Exception {
    context.checking(privateKeyExpectations());
    context.checking(certificateExpectations());
    context.checking(expirationExpectations(new Date(1), new Date(0)));    
    context.checking(new Expectations() { { 
      oneOf(bag).findAuthorityCertificates(certificate);
      will(returnValue(Collections.emptyList()));
      oneOf(errors).addWarning(with(containsString("Incomplete")), 
          with(emptyArray()));
    } });

    importer.validateAndImport(PASSPHRASE, errors);
  }

  @Test
  public void testVerifyWhenAuthorityChainIncomplete() throws Exception {
    context.checking(privateKeyExpectations());
    context.checking(certificateExpectations());
    context.checking(expirationExpectations(new Date(1), new Date(0)));
    context.checking(new Expectations() { { 
      oneOf(bag).findAuthorityCertificates(certificate);
      will(returnValue(Collections.singletonList(certificate)));
      oneOf(certificate).isSelfSigned();
      will(returnValue(false));
      oneOf(errors).addWarning(with(containsString("Incomplete")), 
          with(emptyArray()));
    } });
  
    importer.validateAndImport(PASSPHRASE, errors);
  }

  private Expectations privateKeyExpectations() {
    return new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
    } };
  }

  private Expectations certificateExpectations() { 
    return new Expectations() { { 
      allowing(privateKey).setProtectionParameter(PASSPHRASE);
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(returnValue(certificate));
      allowing(certificate).getSubject();
      will(returnValue(new X500Principal("cn=Some Name")));
      allowing(certificate).getIssuer();
      will(returnValue(new X500Principal("cn=Some Name")));
      allowing(certificate).getSerialNumber();
      will(returnValue(BigInteger.ZERO));
      allowing(certificate).getSerialNumber();
      will(returnValue(BigInteger.ZERO));
      allowing(certificate).getNotBefore();
      will(returnValue(new Date()));
    } };
  }

  private Expectations expirationExpectations(final Date notAfterDate, 
      final Date currentDate) {
    return new Expectations() { { 
      allowing(certificate).getNotAfter();
      will(returnValue(notAfterDate));
      oneOf(timeOfDayService).getCurrent();
      will(returnValue(currentDate));
    } };
  }

}
