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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.TagRepository;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;

/**
 * Unit tests for {@link ConcreteImportService}.
 *
 * @author Carl Harris
 */
public class ConcreteImportServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  private final String groupName = "someGroup";
  private final String loginName = "someUser";
  private final String encodedSecretKey = "someSecretKey";
  private final String encodedPrivateKey = "somePrivateKey";
  private final char[] password = new char[0];

  @Mock
  private CredentialImporterFactory importerFactory;
  
  @Mock
  private CredentialImporter importer;
  
  @Mock
  private ImportDetails details;
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialKey credentialKey;
  
  @Mock
  private ProtectionParameters protection;

  @Mock
  private UserProfile user;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserGroupMember groupMember;
  
  @Mock
  private SecretKeyWrapper encryptedSecretKey;
  
  @Mock
  private SecretKey secretKey;
  
  @Mock
  private PrivateKeyWrapper encryptedPrivateKey;
  
  @Mock
  private PrivateKey privateKey;
  
  @Mock
  private PrivateKeyWrapper credentialPrivateKey;
  
  @Mock
  private Errors errors;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private TagRepository tagRepository;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  protected UserGroupMemberRepository groupMemberRepository;
  
  @Mock
  private SecretKeyDecoder secretKeyDecoder;
  
  @Mock
  private PrivateKeyDecoder privateKeyDecoder;
  
  @Mock
  private PrivateKeyEncryptionService privateKeyEncryptionService;

  
  public ConcreteImportService importService = new ConcreteImportService();
  
  @Before
  public void setUp() throws Exception {
    importService.importerFactory = importerFactory;
    importService.credentialRepository = credentialRepository;
    importService.tagRepository = tagRepository;
    importService.groupRepository = groupRepository;
    importService.groupMemberRepository = groupMemberRepository;
    importService.secretKeyDecoder = secretKeyDecoder;
    importService.privateKeyDecoder = privateKeyDecoder;
    importService.privateKeyEncryptionService = privateKeyEncryptionService;
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
    
    List<FileContentModel> emptyList = Collections.emptyList();
    importService.prepareImport(emptyList, errors);
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
    
    List<FileContentModel> emptyList = Collections.singletonList(file);
    importService.prepareImport(emptyList, errors);
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
    
    List<FileContentModel> emptyList = Collections.singletonList(file);
    importService.prepareImport(emptyList, errors);
  }
  
  @Test(expected = ImportException.class)
  public void testCreateCredentialWhenError() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importer).validate(with(same(errors)));
      will(throwException(new ImportException()));
    } });    
    
    importService.createCredential(importer, errors);
  }

  @Test
  public void testCreateCredentialWhenSuccessful() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importer).validate(with(same(errors)));
      oneOf(importer).build();
      will(returnValue(credential));
    } });    
    
    assertThat(importService.createCredential(importer, errors), 
        is(sameInstance(credential)));    
  }

  @Test
  public void testProtectCredential() throws Exception {        
    context.checking(protectionExpectations(groupName, loginName, password));
    context.checking(userPrivateKeyExpectations(
        returnValue(privateKey)));
    context.checking(groupSecretKeyExpectations());
    context.checking(credentialKeyExpectations());
    context.checking(new Expectations() { {
      oneOf(groupMemberRepository).findByGroupAndLoginName(
          with(same(groupName)), with(same(loginName)));
      will(returnValue(groupMember));
      oneOf(groupMember).getGroup();
      will(returnValue(group));
      oneOf(credential).setOwner(with(same(group)));
    } });
    
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  @Test(expected = NoSuchGroupException.class)
  public void testProtectCredentialWhenGroupNotFound() throws Exception {
    context.checking(protectionExpectations(groupName, loginName, password));
    context.checking(new Expectations() { {
      oneOf(groupMemberRepository).findByGroupAndLoginName(
          with(same(groupName)), with(same(loginName)));
      will(returnValue(null)); 
      oneOf(errors).addError(with("owner"), 
          with(containsString("NotFound")),
          with(emptyArray()));
    } });
    
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testProtectCredentialWhenPasswordIncorrect() throws Exception {
    context.checking(protectionExpectations(groupName, loginName, password));
    context.checking(userPrivateKeyExpectations(
        throwException(new IncorrectPassphraseException())));
    context.checking(new Expectations() { {
      oneOf(groupMemberRepository).findByGroupAndLoginName(
          with(same(groupName)), with(same(loginName)));
      will(returnValue(groupMember)); 
      oneOf(errors).addError(with("password"), 
          with(containsString("Incorrect")),
          with(emptyArray()));
    } });
    
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  private Expectations protectionExpectations(
      final String groupName, final String loginName, final char[] password) {
    return new Expectations() { { 
      allowing(protection).getGroupName();
      will(returnValue(groupName));
      allowing(protection).getLoginName();
      will(returnValue(loginName));
      allowing(protection).getPassword();
      will(returnValue(password));
    } };
  }
  
  private Expectations userPrivateKeyExpectations(
      final Action outcome) {
    return new Expectations() { { 
      oneOf(groupMember).getUser();
      will(returnValue(user));
      oneOf(user).getPrivateKey();
      will(returnValue(encodedPrivateKey));
      oneOf(privateKeyDecoder).decode(with(same(encodedPrivateKey)));
      will(returnValue(encryptedPrivateKey));
      oneOf(encryptedPrivateKey).setProtectionParameter(with(same(password)));
      oneOf(encryptedPrivateKey).derive();
      will(outcome);
      allowing(encryptedSecretKey).setPrivateKey(with(privateKey));
    } };
  }
  
  private Expectations groupSecretKeyExpectations() { 
    return new Expectations() { { 
      oneOf(groupMember).getSecretKey();
      will(returnValue(encodedSecretKey));
      oneOf(secretKeyDecoder).decode(with(same(encodedSecretKey)));
      will(returnValue(encryptedSecretKey));
      oneOf(encryptedSecretKey).derive();
      will(returnValue(secretKey));
    } };
  }
  
  private Expectations credentialKeyExpectations() {
    return new Expectations() { { 
      oneOf(importer).getDetails();
      will(returnValue(details));
      oneOf(details).getPrivateKey();
      will(returnValue(credentialPrivateKey));
      oneOf(privateKeyEncryptionService).encrypt(
          with(same(credentialPrivateKey)), with(same(secretKey)));
      will(returnValue(credentialPrivateKey));
      oneOf(credential).getPrivateKey();
      will(returnValue(credentialKey));
      oneOf(credentialPrivateKey).getContent();
      will(returnValue(encodedPrivateKey));
      oneOf(credentialKey).setContent(with(same(encodedPrivateKey)));
    } };
  }

  @Test
  public void testSaveCredential() throws Exception {
  
    context.checking(new Expectations() { { 
      oneOf(credentialRepository).add(with(same(credential)));
    } });
    
    importService.saveCredential(credential, errors);
  }
  
  @Test
  public void testResolveTagWhenTagFound() throws Exception {
    final String token = "someTag";
    final Tag tag = context.mock(Tag.class);
    
    context.checking(new Expectations() { { 
      oneOf(tagRepository).findByTagText(token);
      will(returnValue(tag));
    } });
    
    assertThat(importService.resolveTags(new String[] { token }),
        contains(tag));
  }

  @Test
  public void testResolveTagWhenTagNotFound() throws Exception {
    final String token = "someTag";
    final Tag tag = context.mock(Tag.class);
    
    context.checking(new Expectations() { { 
      oneOf(tagRepository).findByTagText(with(same(token)));
      will(returnValue(null));
      oneOf(tagRepository).newTag(with(same(token)));
      will(returnValue(tag));
    } });
    
    assertThat(importService.resolveTags(new String[] { token }),
        contains(tag));
  }

  @Test
  public void testGetGroupMemberships() throws Exception {
    final String loginName = "someUser";
    final Set<? extends UserGroup> groupMemberships = 
        new HashSet<>();
        
    context.checking(new Expectations() { { 
      oneOf(groupRepository).findByLoginName(with(same(loginName)));
      will(returnValue(groupMemberships));
    } });
    
    assertThat((Set) importService.getGroupMemberships(loginName), 
        is(sameInstance((Set) groupMemberships)));
  }

}


