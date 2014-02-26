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
package org.soulwing.credo.service.exporter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.archive.ArchiveBuilder;

/**
 * Unit tests for {@link PemArchiveExporter}.
 *
 * @author Carl Harris
 */
public class PemArchiveExporterTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private ExportRequest request;

  @Mock
  private Credential credential;
  
  @Mock
  private CredentialKey privateKey;
  
  @Mock
  private CredentialCertificate certificate;
  
  @Mock
  private CredentialCertificate authority;
  
  @Mock
  private ArchiveBuilder archiveBuilder;
  
  private String fileName = "fileName";
  
  private Reader privateKeyContent = new StringReader("content");
  
  private Reader certificateContent = new StringReader("content");
  
  private Reader authorityContent = new StringReader("content");

  private byte[] archive = new byte[] { 0, 1, 2, 3 };
  
  private PemArchiveExporter exporter;
  
  @Before
  public void setUp() throws Exception {
    exporter = new PemArchiveExporter(archiveBuilder);
  }
  
  @Test
  public void testExportCredential() throws Exception {
    final List<CredentialCertificate> certificates = new ArrayList<>();
    certificates.add(certificate);
    certificates.add(authority);
    
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newAuthorityArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request));
  }

  @Test
  public void testExportCredentialWithNoAuthorities() throws Exception {
    List<CredentialCertificate> certificates = 
        Collections.singletonList(certificate);
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newCertificateArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request));
  }

  @Test
  public void testExportCredentialWithNoCertificates() throws Exception {
    List<CredentialCertificate> certificates = Collections.emptyList();
    context.checking(newCredentialExpectations(certificates));
    context.checking(newPrivateKeyArchiverExpectations());
    context.checking(newBuildArchiveExpectations());
    
    validatePreparation(exporter.exportCredential(request));
  }


  private void validatePreparation(ExportPreparation preparation)
      throws Exception {
    assertThat(preparation, is(not(nullValue())));
    assertThat(preparation.getContentType(), 
        is(equalTo(PemArchiveExporter.CONTENT_TYPE)));
    assertThat(preparation.getCharacterEncoding(),
        is(equalTo(PemArchiveExporter.CHARACTER_ENCODING)));
    assertThat(preparation.getFileName(), is(equalTo(fileName)));
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    preparation.writeContent(bos);
    assertThat(bos.toByteArray(), is(equalTo(archive)));
  }

  private Expectations newCredentialExpectations (
      final List<CredentialCertificate> certificates) throws Exception {
    return new Expectations() { { 
      allowing(request).getCredential();
      will(returnValue(credential));
      oneOf(credential).getPrivateKey();
      will(returnValue(privateKey));
      oneOf(privateKey).getContent();
      will(returnValue(privateKeyContent));
      oneOf(credential).getCertificates();
      will(returnValue(certificates));
      allowing(certificate).getContent();
      will(returnValue(certificateContent));
      allowing(authority).getContent();
      will(returnValue(authorityContent));
    } };
  }

  private Expectations newPrivateKeyArchiverExpectations() throws IOException {
    return new Expectations() { { 
      oneOf(archiveBuilder).beginEntry(
          with(PemArchiveExporter.KEY_ENTRY_NAME), 
          with(PemArchiveExporter.CHARACTER_ENCODING));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).addContent(with(same(privateKeyContent)));
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
      oneOf(archiveBuilder).addContent(with(same(certificateContent)));
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
      oneOf(archiveBuilder).addContent(with(same(authorityContent)));
      will(returnValue(archiveBuilder));
      oneOf(archiveBuilder).endEntry();
      will(returnValue(archiveBuilder));
    } };
  }

  private Expectations newBuildArchiveExpectations() throws IOException {
    return new Expectations() { {
      oneOf(request).getFileName();
      will(returnValue(fileName));
      oneOf(archiveBuilder).build();
      will(returnValue(archive));
    } };
  }
  
}
