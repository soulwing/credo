/*
 * File created on Feb 26, 2014 
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
package org.soulwing.credo.service.exporter.archive;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.jmock.Expectations.returnValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.archive.ArchiveBuilder;
import org.soulwing.credo.service.credential.ExportPreparation;
import org.soulwing.credo.service.credential.ExportRequest;
import org.soulwing.credo.service.crypto.PKCS8EncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * Unit tests for {@link PemArchiveExporter}.
 *
 * @author Carl Harris
 */
public class PemArchiveExporterTest {

  private static final String FILE_NAME = "fileName";
  
  private static final String CONTENT_TYPE = "contentType";
  
  private static final String SUFFIX = "suffix";
  
  private static final String PRIVATE_KEY_CONTENT = "privateKey";
  
  private static final String CERT_CONTENT = "certificate";
  
  private static final String AUTHORITY_CONTENT = "authority"; 

  private static final byte[] ARCHIVE = new byte[] { 0, 1, 2, 3 };
  
  private static final Password PASSWORD = 
      new Password("password".toCharArray());
  
  private static final Password EMPTY_PASSWORD = Password.EMPTY;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  

  @Mock
  private ExportRequest request;

  @Mock
  private Credential credential;
  
  @Mock
  private CredentialKey credentialKey;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  @Mock
  private CredentialCertificate certificate;
  
  @Mock
  private CredentialCertificate authority;
  
  @Mock
  private ArchiveBuilder archiveBuilder;
  
  @Mock
  private PKCS8EncryptionService pkcs8EncryptionService;

  @Mock
  private Instance<PemArchiveVariant> variants;
  
  @Mock
  private Iterator<PemArchiveVariant> variantIterator;
  
  @Mock
  private PemArchiveVariant variant;
  
  private PemArchiveExporter exporter = new PemArchiveExporter();
  
  @Before
  public void setUp() throws Exception {
    exporter.setVariants(variants);
    exporter.pkcs8EncryptionService = pkcs8EncryptionService;
  }
  
  @Test
  public void testExportCredentialWithNoPassphrase() throws Exception {
    final List<CredentialCertificate> certificates = new ArrayList<>();
    certificates.add(certificate);
    certificates.add(authority);
    
    context.checking(newPassphraseExpectations(returnValue(null)));
    context.checking(newFindVariantExpectations(returnValue(variant)));
    context.checking(newUseVariantExpectations());
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newAuthorityArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request, privateKey));
  }

  @Test
  public void testExportCredentialWithEmptyPassphrase() throws Exception {
    final List<CredentialCertificate> certificates = new ArrayList<>();
    certificates.add(certificate);
    certificates.add(authority);
    
    context.checking(newPassphraseExpectations(returnValue(EMPTY_PASSWORD)));
    context.checking(newFindVariantExpectations(returnValue(variant)));
    context.checking(newUseVariantExpectations());
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newAuthorityArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request, privateKey));
  }

  @Test
  public void testExportCredentialWithPassphrase() throws Exception {
    final List<CredentialCertificate> certificates = new ArrayList<>();
    certificates.add(certificate);
    certificates.add(authority);
    
    context.checking(newPassphraseExpectations(returnValue(PASSWORD)));
    context.checking(newFindVariantExpectations(returnValue(variant)));
    context.checking(newUseVariantExpectations());
    context.checking(newCredentialExpectations(certificates));
    context.checking(newEncryptionServiceExpectations(PASSWORD));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newAuthorityArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request, privateKey));
  }


  @Test
  public void testExportCredentialWithNoAuthorities() throws Exception {
    List<CredentialCertificate> certificates = 
        Collections.singletonList(certificate);
    context.checking(newPassphraseExpectations(returnValue(null)));
    context.checking(newFindVariantExpectations(returnValue(variant)));
    context.checking(newUseVariantExpectations());
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request, privateKey));
  }

  @Test
  public void testExportCredentialWithNoCertificates() throws Exception {
    List<CredentialCertificate> certificates = Collections.emptyList();
    context.checking(newPassphraseExpectations(returnValue(null)));
    context.checking(newFindVariantExpectations(returnValue(variant)));
    context.checking(newUseVariantExpectations());
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request, privateKey));
  }

  private void validatePreparation(ExportPreparation preparation)
      throws Exception {
    assertThat(preparation, is(not(nullValue())));
    assertThat(preparation.getContentType(), 
        is(equalTo(CONTENT_TYPE)));
    assertThat(preparation.getCharacterEncoding(),
        is(equalTo(PemArchiveExporter.CHARACTER_ENCODING)));
    assertThat(preparation.getFileName(), is(equalTo(FILE_NAME)));
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    preparation.writeContent(bos);
    assertThat(bos.toByteArray(), is(equalTo(ARCHIVE)));
  }

  private Expectations newPassphraseExpectations(final Action outcome) { 
    return new Expectations() { { 
      allowing(request).getExportPassphrase();
      will(outcome);
    } };
  }
  
  private Expectations newFindVariantExpectations(final Action outcome) {
    final String variantId = "variantId";
    return new Expectations() { {
      oneOf(request).getVariant();
      will(returnValue(variantId));
      oneOf(variants).iterator();
      will(returnValue(variantIterator));
      atMost(2).of(variantIterator).hasNext();
      will(onConsecutiveCalls(returnValue(true), returnValue(false)));
      oneOf(variantIterator).next();
      will(outcome);
      allowing(variant).getId();
      will(returnValue(variantId));
    } };
  }

  private Expectations newUseVariantExpectations() {
    return new Expectations() { { 
      oneOf(variant).newArchiveBuilder();
      will(returnValue(archiveBuilder));
    } };
  }
  

  private Expectations newEncryptionServiceExpectations(
      final Password password) {
    return new Expectations() { { 
      oneOf(pkcs8EncryptionService).encrypt(with(same(privateKey)), 
          with(same(password)));
      will(returnValue(privateKey));
    } };
  }
  
  private Expectations newCredentialExpectations (
      final List<CredentialCertificate> certificates) throws Exception {
    return new Expectations() { { 
      allowing(request).getCredential();
      will(returnValue(credential));
      oneOf(credential).getCertificates();
      will(returnValue(certificates));
      allowing(certificate).getContent();
      will(returnValue(CERT_CONTENT));
      allowing(authority).getContent();
      will(returnValue(AUTHORITY_CONTENT));
    } };
  }

  private Expectations newPrivateKeyArchiverExpectations() throws IOException {
    return new Expectations() { { 
      oneOf(archiveBuilder).beginEntry(
          with(PemArchiveExporter.KEY_ENTRY_NAME), 
          with(PemArchiveExporter.CHARACTER_ENCODING));
      will(returnValue(archiveBuilder));
      oneOf(privateKey).getContent();
      will(returnValue(PRIVATE_KEY_CONTENT));
      oneOf(archiveBuilder).addContent(with(same(PRIVATE_KEY_CONTENT)));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).endEntry();
      will(returnValue(archiveBuilder));
    } };
  }

  private Expectations newCertificateArchiverExpectations() throws IOException {
    return new Expectations() { { 
      oneOf(archiveBuilder).beginEntry(
          with(PemArchiveExporter.CERT_ENTRY_NAME), 
          with(PemArchiveExporter.CHARACTER_ENCODING));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).addContent(with(same(CERT_CONTENT)));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).endEntry();
      will(returnValue(archiveBuilder));
    } };
  }

  private Expectations newAuthorityArchiverExpectations() throws IOException {
    return new Expectations() { { 
      oneOf(archiveBuilder).beginEntry(
          with(PemArchiveExporter.CA_CERTS_ENTRY_NAME), 
          with(PemArchiveExporter.CHARACTER_ENCODING));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).addContent(with(same(AUTHORITY_CONTENT)));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).endEntry();
      will(returnValue(archiveBuilder));
    } };
  }

  private Expectations newBuildArchiveExpectations() throws IOException {
    return new Expectations() { {
      oneOf(variant).getSuffix();
      will(returnValue(SUFFIX));
      oneOf(variant).getContentType();
      will(returnValue(CONTENT_TYPE));
      oneOf(request).getSuffixedFileName(with(SUFFIX));
      will(returnValue(FILE_NAME));
      oneOf(archiveBuilder).build();
      will(returnValue(ARCHIVE));
    } };
  }
  
}
