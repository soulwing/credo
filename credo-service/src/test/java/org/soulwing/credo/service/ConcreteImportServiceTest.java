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
import java.util.Collections;
import java.util.List;

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
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.TagRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.importer.CredentialImporterFactory;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.protect.GroupAccessException;
import org.soulwing.credo.service.protect.UserAccessException;

/**
 * Unit tests for {@link ConcreteImportService}.
 *
 * @author Carl Harris
 */
public class ConcreteImportServiceTest {

  private static final String GROUP_NAME = "someGroup";

  private static final String LOGIN_NAME = "someUser";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
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
  private UserGroup group;
  
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
    importService.tagRepository = tagRepository;
    importService.groupRepository = groupRepository;
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
    context.checking(requestDetailsExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(returnValue(null)));
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  @Test
  public void testProtectCredentialWhenGroupNotFound() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(null)));
    context.checking(createOwnerGroupExpectations());
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(returnValue(null)));
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testProtectCredentialWhenPasswordIncorrect() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(
        throwException(new UserAccessException(new Exception()))));
    context.checking(passwordErrorExpectations());
    importService.protectCredential(credential, importer, protection, errors);
  }

  @Test(expected = AccessDeniedException.class)
  public void testProtectCredentialWhenUserNotInGroup() throws Exception {
    context.checking(requestDetailsExpectations());
    context.checking(findOwnerGroupExpectations(returnValue(group)));
    context.checking(storeOwnerExpectations());
    context.checking(protectionExpectations(
        throwException(new GroupAccessException("some message"))));
    context.checking(accessDeniedErrorExpectations());
    importService.protectCredential(credential, importer, protection, errors);
  }
  
  private Expectations requestDetailsExpectations() {
    return new Expectations() { { 
      allowing(importer).getDetails();
      will(returnValue(details));
      allowing(details).getPrivateKey();
      will(returnValue(credentialPrivateKey));     
    } };
  }

  private Expectations findOwnerGroupExpectations(final Action outcome)
      throws Exception {
    return new Expectations() { { 
      allowing(protection).getGroupName();
      will(returnValue(GROUP_NAME));
      allowing(userContextService).getLoginName();
      will(returnValue(LOGIN_NAME));
      oneOf(groupRepository).findByGroupName(with(same(GROUP_NAME)), 
          with(same(LOGIN_NAME)));
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
      oneOf(groupRepository).findByGroupName(with(same(GROUP_NAME)), 
          with(same(LOGIN_NAME)));
      will(returnValue(group));
    } };
  }
  
  private Expectations protectionExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(protectionService).protect(with(same(credential)), 
          with(same(credentialPrivateKey)), with(same(protection)));
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
  public void testUserIsNotMemberOfSelfGroupOnly() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).findAllGroups();
      will(returnValue(Collections.singleton(
          context.mock(GroupDetail.class))));
    } });
    
    assertThat(importService.isMemberOfSelfGroupOnly(), is(false));
  }

  @Test
  public void testUserIsMemberOfSelfGroupOnly() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).findAllGroups();
      will(returnValue(Collections.emptySet()));
    } });
    
    assertThat(importService.isMemberOfSelfGroupOnly(), is(true));
  }


}


