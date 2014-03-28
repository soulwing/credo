/*
 * File created on Mar 8, 2014 
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
package org.soulwing.credo.service.exporter.keystore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Iterator;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.CertificateWrapperFactory;
import org.soulwing.credo.service.crypto.KeyStoreBuilder;
import org.soulwing.credo.service.crypto.KeyStoreBuilderFactory;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * Unit tests for {@link KeyStoreExporter}.
 *
 * @author Carl Harris
 */
public class KeyStoreExporterTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  private final String type = "keyStoreType";
  private final String contentType = "contentType";
  private final String fileName = "fileName";
  private final String suffix = "suffix";
  private final Password password = Password.EMPTY;
  private final byte[] content = { 0, 1, 2, 3 };
  
  @Mock
  private CertificateWrapperFactory certificateFactory;
 
  @Mock
  private KeyStoreBuilderFactory keyStoreBuilderFactory;
  
  @Mock
  private Instance<KeyStoreVariant> variants;

  @Mock
  private Iterator<KeyStoreVariant> variantIterator;
  
  @Mock
  private KeyStoreVariant variant;
  
  @Mock
  private KeyStoreBuilder keyStoreBuilder;

  @Mock
  private PrivateKeyWrapper privateKey;

  @Mock
  private ExportRequest request;
  
  @Mock
  private Credential credential;
  
  @Mock
  private CertificateWrapper certificate;
  
  @Mock
  private CredentialCertificate credentialCertificate;
  
  private KeyStoreExporter exporter = new KeyStoreExporter();
  
  @Before
  public void setUp() throws Exception {
    exporter.setVariants(variants);
    exporter.certificateFactory = certificateFactory;
    exporter.keyStoreBuilderFactory = keyStoreBuilderFactory;    
  }
  
  @Test
  public void testExportCredential() throws Exception {
    context.checking(requestExpectations());
    context.checking(variantExpectations());
    context.checking(credentialExpectations());
    context.checking(keyStoreBuilderExpectations());
    ExportPreparation preparation = exporter.exportCredential(
        request, privateKey);
    assertThat(preparation.getContentType(), is(equalTo(contentType)));
    assertThat(preparation.getFileName(), is(equalTo(fileName)));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    preparation.writeContent(bos);
    assertThat(bos.toByteArray(), is(equalTo(content)));
  }
  
  private Expectations requestExpectations() {
    return new Expectations() { { 
      allowing(request).getVariant();
      will(returnValue(type));
      allowing(request).getExportPassphrase();
      will(returnValue(password));
      allowing(request).getFileName();
      will(returnValue(fileName));
      allowing(request).getCredential();
      will(returnValue(credential));
      allowing(request).getSuffixedFileName(suffix);
      will(returnValue(fileName));
    } };
  }
  
  private Expectations variantExpectations() { 
    return new Expectations() { { 
      oneOf(variants).iterator();
      will(returnValue(variantIterator));
      oneOf(variantIterator).hasNext();
      will(returnValue(true));
      oneOf(variantIterator).next();
      will(returnValue(variant));
      oneOf(variant).getId();
      will(returnValue(type));
      allowing(variant).getType();
      will(returnValue(type));
      allowing(variant).getContentType();
      will(returnValue(contentType));
      allowing(variant).getSuffix();
      will(returnValue(suffix));
    } };
  }

  private Expectations credentialExpectations() { 
    return new Expectations() { { 
      allowing(credential).getCertificates();
      will(returnValue(Collections.singletonList(credentialCertificate)));
      oneOf(certificateFactory).newCertificateWrapper(
          with(same(credentialCertificate)));
      will(returnValue(certificate));
    } } ;    
  }
  
  private Expectations keyStoreBuilderExpectations() 
      throws Exception {
    return new Expectations() { { 
      oneOf(keyStoreBuilderFactory).newBuilder(with(type));
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).beginEntry(with(fileName));
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).setPrivateKey(with(same(privateKey)));
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).setPassphrase(with(same(password)));
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).addCertificate(with(same(certificate)));
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).endEntry();
      will(returnValue(keyStoreBuilder));
      oneOf(keyStoreBuilder).build(with(same(password)));
      will(returnValue(content));
    } };
  }
  
}
