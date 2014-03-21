/*
 * File created on Feb 16, 2014 
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

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialCertificateBuilder;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;
import org.soulwing.credo.service.protect.CredentialProtectionService;

/**
 * Unit tests for {@link ConcreteImportService}.
 *
 * @author Carl Harris
 */
public class ConcreteImportServiceTest {



  private static final String CREDENTIAL_NAME = "credentialName";

  private static final String CREDENTIAL_NOTE = "credentialNote";
  
  private static final String CREDENTIAL_TAG = "credentialTag";

  private static final String[] CREDENTIAL_TAGS = new String[] { CREDENTIAL_TAG };
  
  private static final String ENCODED_PRIVATE_KEY = "encodedPrivateKey";

  private static final BigInteger SERIAL_NUMBER = BigInteger.ZERO;

  private static final X500Principal SUBJECT_X500_NAME = 
      new X500Principal("cn=Some Subject");
  
  private static final X500Principal ISSUER_X500_NAME = 
      new X500Principal("cn=Some Issuer");
  
  private static final String ENCODED_CERTIFICATE = "encodedCertificate";

  private static final Date ISSUANCE = new Date();
  
  private static final Date EXPIRATION = new Date();

  private static final String ISSUER = "issuer";

  private static final String GROUP_NAME = "someGroup";

  private static final String LOGIN_NAME = "someUser";

  private static final Password PASSPHRASE = new Password(new char[0]);

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialImporterFactory importerFactory;
  
  @Mock
  private CredentialImporter importer;
  
  @Mock
  private CredentialBuilderFactory credentialBuilderFactory;
  
  @Mock
  private CredentialBuilder credentialBuilder;
  
  @Mock
  private CredentialCertificateBuilder certificateBuilder;
  
  @Mock
  private ImportDetails details;
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialCertificate credentialCertificate;
  
  @Mock
  private ProtectionParameters protection;

  @Mock
  private UserGroup group;
  
  @Mock
  private UserGroupMember member;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  @Mock
  private CertificateWrapper certificate;
  
  @Mock
  private Errors errors;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private Tag tag;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private UserGroupMemberRepository memberRepository;
  
  @Mock
  private TagService tagService;
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private UserContextService userContextService;
  
  @Mock
  private CredentialProtectionService protectionService;
  
  public ConcreteImportService importService = new ConcreteImportService();
  
  @Before
  public void setUp() throws Exception {
    importService.importerFactory = importerFactory;
    importService.credentialRepository = credentialRepository;
    importService.credentialBuilderFactory = credentialBuilderFactory;
    importService.tagService = tagService;
    importService.groupRepository = groupRepository;
    importService.memberRepository = memberRepository;
    importService.groupService = groupService;
    importService.userContextService = userContextService;
    importService.protectionService = protectionService;
  }
  
  @Test(expected = ImportException.class)
  public void testPrepareImportWithNoFiles() throws Exception {
    context.checking(new Expectations() { {
      oneOf(importerFactory).newImporter();
      will(returnValue(importer));
      oneOf(errors).addError(
          with(containsString("Required")),
          with(emptyArray()));
    } });
    
    List<FileContentModel> files = Collections.emptyList();
    importService.prepareImport(files, errors, PASSPHRASE);
  }
  
  @Test(expected = ImportException.class)
  public void testPrepareImportWhenFileReadError() throws Exception {
    final FileContentModel file = context.mock(FileContentModel.class);
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    final String name = "someFileName";
    context.checking(new Expectations() { {
      oneOf(file).getInputStream();
      will(returnValue(inputStream));
      oneOf(file).getName();
      will(returnValue(name));
      oneOf(importerFactory).newImporter();
      will(returnValue(importer));
      oneOf(importer).loadFile(with(same(inputStream)));
      will(throwException(new IOException()));
      oneOf(errors).addError(with("file0"),
          with(containsString("Error")),
          (Object[]) with(arrayContaining(name)));
      oneOf(errors).hasErrors();
      will(returnValue(true));
    } });
    
    List<FileContentModel> files = Collections.singletonList(file);
    importService.prepareImport(files, errors, PASSPHRASE);
  }
  
  @Test(expected = ImportException.class)
  public void testPrepareImportWithEmptyFile() throws Exception {
    final FileContentModel file = context.mock(FileContentModel.class);
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    final String name = "someFileName";
    context.checking(new Expectations() { {
      oneOf(file).getInputStream();
      will(returnValue(inputStream));
      oneOf(file).getName();
      will(returnValue(name));
      oneOf(importerFactory).newImporter();
      will(returnValue(importer));
      oneOf(importer).loadFile(with(same(inputStream)));
      will(throwException(new NoContentException()));
      oneOf(errors).addError(with("file0"),
          with(containsString("Content")),
          (Object[]) with(arrayContaining(name)));
      oneOf(errors).hasErrors();
      will(returnValue(true));
    } });
    
    List<FileContentModel> files = Collections.singletonList(file);
    importService.prepareImport(files, errors, PASSPHRASE);
  }

  @Test
  public void testPrepareImportSuccess() throws Exception {
    final FileContentModel file = context.mock(FileContentModel.class);
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { {
      oneOf(file).getInputStream();
      will(returnValue(inputStream));
      oneOf(importerFactory).newImporter();
      will(returnValue(importer));
      oneOf(importer).loadFile(with(same(inputStream)));
      oneOf(errors).hasErrors();
      will(returnValue(false));
      oneOf(importer).validateAndImport(with(PASSPHRASE), with(same(errors)));
    } });
    
    List<FileContentModel> files = Collections.singletonList(file);
    importService.prepareImport(files, errors, PASSPHRASE);
  }

  @Test
  public void testProtectCredential() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(credentialBuilderExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(returnValue(null)));
    importService.createCredential(details, protection, errors);
  }
  
  @Test
  public void testProtectCredentialWhenGroupNotFound() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(credentialBuilderExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(null)));
    context.checking(createOwnerGroupExpectations());
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(returnValue(null)));
    importService.createCredential(details, protection, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testProtectCredentialWhenPasswordIncorrect() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(credentialBuilderExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(
        throwException(new UserAccessException(new Exception()))));
    context.checking(passwordErrorExpectations());
    importService.createCredential(details, protection, errors);
  }

  @Test(expected = AccessDeniedException.class)
  public void testProtectCredentialWhenUserNotInGroup() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(credentialBuilderExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(
        throwException(new GroupAccessException("some message"))));
    context.checking(accessDeniedErrorExpectations());
    importService.createCredential(details, protection, errors);
  }
  
  private Expectations requestDetailsExpectations() throws Exception {
    return new Expectations() { {
      allowing(details).getPrivateKey();
      will(returnValue(privateKey));
      allowing(details).getIssuerCommonName();
      will(returnValue(ISSUER));
      allowing(details).getNotAfter();
      will(returnValue(EXPIRATION));
      allowing(details).getCertificates();      
      will(returnValue(Collections.singletonList(certificate)));
      allowing(privateKey).getContent();
      will(returnValue(ENCODED_PRIVATE_KEY));
      allowing(certificate).getContent();
      will(returnValue(ENCODED_CERTIFICATE));
      allowing(certificate).getSubject();
      will(returnValue(SUBJECT_X500_NAME));
      allowing(certificate).getIssuer();
      will(returnValue(ISSUER_X500_NAME));
      allowing(certificate).getSerialNumber();
      will(returnValue(SERIAL_NUMBER));
      allowing(certificate).getNotAfter();
      will(returnValue(EXPIRATION));
      allowing(certificate).getNotBefore();
      will(returnValue(ISSUANCE));
      allowing(details).getName();
      will(returnValue(CREDENTIAL_NAME));
      allowing(details).getNote();
      will(returnValue(CREDENTIAL_NOTE));
      allowing(details).getTags();
      will(returnValue(CREDENTIAL_TAGS));
    } };
  }

  @SuppressWarnings("unchecked")
  private Expectations credentialBuilderExpectations() {
    return new Expectations() { { 
      oneOf(credentialBuilderFactory).newCredentialBuilder();
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setName(with(CREDENTIAL_NAME));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setNote(with(CREDENTIAL_NOTE));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setTags((Collection<Tag>) with(contains(tag)));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setExpiration(with(EXPIRATION));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setIssuer(with(ISSUER));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).setPrivateKey(with(ENCODED_PRIVATE_KEY));
      will(returnValue(credentialBuilder));
      oneOf(credentialBuilder).build();
      will(returnValue(credential));
      oneOf(credentialBuilderFactory).newCertificateBuilder();
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setSubject(with(SUBJECT_X500_NAME));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setIssuer(with(ISSUER_X500_NAME));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setSerialNumber(with(SERIAL_NUMBER));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setNotBefore(with(ISSUANCE));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setNotAfter(with(EXPIRATION));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).setContent(with(ENCODED_CERTIFICATE));
      will(returnValue(certificateBuilder));
      oneOf(certificateBuilder).build();
      will(returnValue(credentialCertificate));
      oneOf(credentialBuilder).addCertificate(with(credentialCertificate));
      will(returnValue(credentialBuilder));
      oneOf(tagService).resolve(with(CREDENTIAL_TAGS));
      will(returnValue(Collections.singleton(tag)));
    } };
  }

  private Expectations findOwnerGroupExpectations(final Action outcome)
      throws Exception {
    return new Expectations() { { 
      allowing(protection).getGroupName();
      will(returnValue(GROUP_NAME));
      allowing(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME),
          with(LOGIN_NAME));
      will(outcome);
    } };
  }

  private Expectations createOwnerGroupExpectations() throws Exception {
    final GroupEditor editor = context.mock(GroupEditor.class);
    return new Expectations() { {
      oneOf(groupService).newGroup();
      will(returnValue(editor));
      oneOf(editor).setName(with(GROUP_NAME));
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
      oneOf(groupRepository).findByGroupName(with(GROUP_NAME),
          with(LOGIN_NAME));
      will(returnValue(group));
    } };
  }
  
  private Expectations protectionExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(protectionService).protect(with(same(credential)), 
          with(same(privateKey)), with(same(protection)));
      will(outcome);
    } };
  }
  
  private Expectations storeOwnerExpectations() {
    return new Expectations() { { 
      oneOf(credential).setOwner(with(same(group)));
    } };
  }
  
  private Expectations passwordErrorExpectations() { 
    return new Expectations() { { 
      oneOf(errors).addError(with("password"), 
          with(containsString("Incorrect")),
          with(emptyArray()));
    } };
  }

  private Expectations accessDeniedErrorExpectations() { 
    return new Expectations() { { 
      allowing(protection).getGroupName();
      will(returnValue(GROUP_NAME));
      oneOf(errors).addError(with("owner"), 
          with(containsString("Denied")),
          (Object[]) with(arrayContaining(GROUP_NAME)));
    } };
  }

  @Test
  public void testSaveCredential() throws Exception {
  
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).add(with(same(credential)));
    } });
    
    importService.saveCredential(credential, errors);
  }
  
}


