/*
 * File created on Feb 13, 2014 
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
package org.soulwing.credo.facelets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.servlet.http.Part;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.service.AccessDeniedException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportPreparation;
import org.soulwing.credo.service.ImportService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserProfileService;

/**
 * Unit tests for {@link ImportCredentialBean}.
 *
 * @author Carl Harris
 */
public class ImportCredentialBeanTest {

  private static final String SUBJECT_NAME = "subjectName";
  
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() { {
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private Conversation conversation;
  
  @Mock
  private Errors errors;
  
  @Mock
  private ImportService importService;
  
  @Mock
  private UserProfileService profileService;
  
  @Mock
  private UserProfile profile;
    
  @Mock
  private ImportPreparation preparation;
  
  @Mock
  private ImportDetails details;
  
  @Mock
  private Credential credential;
  
  private ImportCredentialBean bean = new ImportCredentialBean();
  
  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.errors = errors;
    bean.importService = importService;
    bean.profileService = profileService;
  }

  @Test
  public void testInit() throws Exception {
    final String expectedPassword = "password";
    context.checking(new Expectations() { { 
      oneOf(profileService).getLoggedInUserProfile();
      will(returnValue(profile));
      oneOf(profile).getPassword();
      will(returnValue(expectedPassword));
    } });
    
    bean.init();
    assertThat(bean.getPasswordFormBean().getExpected(),
        is(equalTo(expectedPassword)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadWithNoFilesSelected() throws Exception {
    context.checking(conversationExpectations());
    context.checking(new Expectations() { {
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(any(List.class)),
          with(errors));
      will(throwException(new ImportException()));
    } });
    
    bean.setFile0(null);
    bean.setFile1(null);
    bean.setFile2(null);
    assertThat(bean.upload(), nullValue());
  }
  
  @Test
  public void testUploadSuccessWithFile0Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, true));
    
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(ImportCredentialBean.PASSPHRASE_OUTCOME_ID));
    assertThat(bean.getPreparation(), sameInstance(preparation));
  }

  @Test
  public void testUploadSuccessWithFile1Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, true));
    
    bean.setFile1(file);
    assertThat(bean.upload(), equalTo(ImportCredentialBean.PASSPHRASE_OUTCOME_ID));
    assertThat(bean.getPreparation(), sameInstance(preparation));
  }
    
  @Test
  public void testUploadSuccessWithFile2Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, true));
    
    bean.setFile2(file);
    assertThat(bean.upload(), equalTo(ImportCredentialBean.PASSPHRASE_OUTCOME_ID));
    assertThat(bean.getPreparation(), sameInstance(preparation));
  }

  @Test
  public void testUploadWhenNoPassphraseRequired() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, false));
    context.checking(new Expectations() { { 
      allowing(preparation).isPassphraseRequired();
      will(returnValue(false));
      oneOf(importService).createCredential(with(preparation), 
          with(errors));
      // throw an exception here to avoid having to deal with credential creation
      will(throwException(new ImportException()));
    } });
      
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(ImportCredentialBean.FAILURE_OUTCOME_ID));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadError() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(errors));
      will(throwException(new ImportException()));
    } });
    
    bean.setFile0(file);
    assertThat(bean.upload(), nullValue());
  }

  @SuppressWarnings("unchecked")
  private Expectations uploadSuccessExpectations(final Part file,
      final boolean passphraseRequired) 
      throws Exception {
    return new Expectations() { {
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(errors));    
      will(returnValue(preparation));
      oneOf(preparation).isPassphraseRequired();
      will(returnValue(passphraseRequired));
    } };
  }
    
  private Expectations conversationExpectations() {
    return new Expectations() { { 
      oneOf(conversation).isTransient();
      will(returnValue(true));
      oneOf(conversation).begin();
    } };
  }
  
  @Test
  public void testIsMemberOfSelfGroupOnlyWhenFalse() throws Exception {
    final UserGroup group1 = context.mock(UserGroup.class, "group1");
    final UserGroup group2 = context.mock(UserGroup.class, "group2");
    final Set<UserGroup> groupMemberships =
        new HashSet<>();
    groupMemberships.add(group1);
    groupMemberships.add(group2);

    context.checking(new Expectations() { { 
      oneOf(importService).getGroupMemberships();
      will(returnValue(groupMemberships));
    } });

    assertThat(bean.isMemberOfSelfGroupOnly(), is(false));
  }
  
  @Test
  public void testIsMemberOfSelfGroupOnlyWhenTrue() throws Exception {
    final UserGroup group = context.mock(UserGroup.class);
    final Set<? extends UserGroup> groupMemberships =
        Collections.singleton(group);
  
    context.checking(new Expectations() { { 
      oneOf(importService).getGroupMemberships();
      will(returnValue(groupMemberships));
    } });
    
    assertThat(bean.isMemberOfSelfGroupOnly(), is(true));
  }

  @Test
  public void testGetTagsWhenNull() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credential).getTags();
      will(returnValue(null));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.getTags().isEmpty(), equalTo(true));
  }

  @Test
  public void testGetTagsWhenEmpty() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credential).getTags();
      will(returnValue(Collections.emptySet()));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.getTags().isEmpty(), equalTo(true));
  }

  @Test
  public void testGetTagsWithOneTag() throws Exception {
    final Tag tag = context.mock(Tag.class);
    context.checking(new Expectations() { { 
      oneOf(credential).getTags();
      will(returnValue(Collections.singleton(tag)));
      oneOf(tag).getText();
      will(returnValue("tag"));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.getTags(), equalTo("tag"));
  }

  @Test
  public void testGetTagsWithTwoTag() throws Exception {
    final Tag tag0 = context.mock(Tag.class, "tag0");
    final Tag tag1 = context.mock(Tag.class, "tag1");
    final Set<Tag> tags = new LinkedHashSet<Tag>();
    tags.add(tag0);
    tags.add(tag1);
    
    context.checking(new Expectations() { { 
      oneOf(credential).getTags();
      will(returnValue(tags));
      oneOf(tag0).getText();
      will(returnValue("tag0"));
      oneOf(tag1).getText();
      will(returnValue("tag1"));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.getTags(), equalTo("tag0,tag1"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSetTagsWithNull() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credential).setTags((Set<Tag>) with(empty()));
    } });
    
    bean.setCredential(credential);
    bean.setTags(null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testSetTagsWithBlank() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(credential).setTags((Set<Tag>) with(empty()));
    } });
    
    bean.setCredential(credential);
    bean.setTags(" ");
  }


  @Test
  public void testSetTagsWithOneTag() throws Exception {
    final Set<Tag> tags = Collections.emptySet();
    context.checking(new Expectations() { { 
      oneOf(importService).resolveTags(with(
          hasItemInArray(equalTo("tag"))));
      will(returnValue(tags));
      oneOf(credential).setTags(with(same(tags)));
    } });
    
    bean.setCredential(credential);
    bean.setTags("tag");
  }

  @Test
  public void testSetTagsWithTwoTags() throws Exception {
    final Set<Tag> tags = Collections.emptySet();
    context.checking(new Expectations() { { 
      oneOf(importService).resolveTags(with(allOf(
          hasItemInArray(equalTo("tag0")), 
          hasItemInArray(equalTo("tag1")))));
      will(returnValue(tags));
      oneOf(credential).setTags(with(same(tags)));
    } });
    
    bean.setCredential(credential);
    bean.setTags("tag0, tag1");
  }

  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(same(errors)));
      oneOf(conversation).end();
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), equalTo(ImportCredentialBean.SUCCESS_OUTCOME_ID));    
  }

  @Test
  public void testSaveImportError() throws Exception {

    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), nullValue());    
  }

  @Test
  public void testCancel() throws Exception {
    context.checking(new Expectations() { {
      oneOf(conversation).isTransient();
      will(returnValue(false));
      oneOf(conversation).end();
    } });
    
    assertThat(bean.cancel(), equalTo(ImportCredentialBean.CANCEL_OUTCOME_ID));
  }
  
  @Test
  public void testValidateWhenSuccess() throws Exception {
    context.checking(new Expectations() { { 
      allowing(preparation).isPassphraseRequired();
      will(returnValue(false));
      oneOf(importService).createCredential(with(same(preparation)), 
          with(same(errors)));
      will(returnValue(credential));
      oneOf(preparation).getDetails();
      will(returnValue(details));
      oneOf(details).getSubject();
      will(returnValue(SUBJECT_NAME));
      oneOf(credential).setName(with(same(SUBJECT_NAME)));
    } });
    
    bean.setPreparation(preparation);
    assertThat(bean.validate(), equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }

  @Test
  public void testValidateWhenError() throws Exception {
    context.checking(new Expectations() { { 
      allowing(preparation).isPassphraseRequired();
      will(returnValue(false));
      oneOf(importService).createCredential(with(same(preparation)), 
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setPreparation(preparation);
    assertThat(bean.validate(), equalTo(ImportCredentialBean.FAILURE_OUTCOME_ID));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testValidateAfterCollectingPassphrase() throws Exception {
    final Part file = context.mock(Part.class);
    final ImportPreparation preparation2 = context.mock(
        ImportPreparation.class, "prepartion2");
    final Password passphrase = new Password(new char[0]);
    context.checking(new Expectations() { { 
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      allowing(preparation).isPassphraseRequired();
      will(returnValue(true));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(errors));
      will(returnValue(preparation2));
      oneOf(preparation).getPassphrase();
      will(returnValue(passphrase));
      oneOf(preparation2).setPassphrase(with(same(passphrase)));
      oneOf(importService).createCredential(with(same(preparation2)), 
          with(same(errors)));
      will(returnValue(credential));
      oneOf(preparation2).getDetails();
      will(returnValue(details));
      oneOf(details).getSubject();
      will(returnValue(SUBJECT_NAME));
      oneOf(credential).setName(with(same(SUBJECT_NAME)));
    } });
    
    bean.setFile0(file);
    bean.setPreparation(preparation);
    assertThat(bean.validate(), equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testValidateWithIncorrectPassphrase() throws Exception {
    final Part file = context.mock(Part.class);
    final ImportPreparation preparation2 = context.mock(
        ImportPreparation.class, "preparation2");
    final Password passphrase = new Password(new char[0]);
    context.checking(new Expectations() { { 
      allowing(file).getInputStream();
      will(returnValue(new ByteArrayInputStream(new byte[0])));
      allowing(preparation).isPassphraseRequired();
      will(returnValue(true));
      oneOf(importService).prepareImport(
          (List<FileContentModel>) with(not(empty())), 
          with(errors));
      will(returnValue(preparation2));
      oneOf(preparation).getPassphrase();
      will(returnValue(passphrase));
      oneOf(preparation2).setPassphrase(with(same(passphrase)));
      oneOf(importService).createCredential(with(same(preparation2)), 
          with(same(errors)));
      will(throwException(new PassphraseException()));
      oneOf(errors).addError(with(containsString("passphrase")),
          with(containsString("Incorrect")), with(emptyArray()));
    } });
    
    bean.setFile0(file);
    bean.setPreparation(preparation);
    assertThat(bean.validate(), 
        equalTo(ImportCredentialBean.PASSPHRASE_OUTCOME_ID));
  }

  @Test
  public void testProtectSuccess() throws Exception {
    context.checking(protectionExpectations(returnValue(null)));    
    bean.setPreparation(preparation);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.CONFIRM_OUTCOME_ID)));    
  }
  
  @Test
  public void testProtectOwnerNotFound() throws Exception {
    context.checking(protectionExpectations(
        throwException(new NoSuchGroupException())));    
    bean.setPreparation(preparation);
    bean.setCredential(credential);
    assertThat(bean.protect(), 
        is(equalTo(ImportCredentialBean.DETAILS_OUTCOME_ID)));        
  }

  @Test
  public void testProtectPasswordIncorrect() throws Exception {
    context.checking(protectionExpectations(
        throwException(new PassphraseException())));
    bean.setPreparation(preparation);
    bean.setCredential(credential);
    assertThat(bean.protect(), is(nullValue()));        
  }

  @Test(expected = RuntimeException.class)
  public void testProtectAccessDenied() throws Exception {
    context.checking(protectionExpectations(
        throwException(new AccessDeniedException())));
    bean.setPreparation(preparation);
    bean.setCredential(credential);
    bean.protect();     
  }

  private Expectations protectionExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(importService).protectCredential(with(same(credential)), 
          with(same(preparation)), 
          with(any(ProtectionParameters.class)), 
          with(same(errors)));
      will(outcome);
    } };
  }
}
