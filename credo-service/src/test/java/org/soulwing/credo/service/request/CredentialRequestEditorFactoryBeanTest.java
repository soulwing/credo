/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.service.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.security.auth.x500.X500Principal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.TagEntity;

/**
 * Unit tests for {@link CredentialRequestEditorFactoryBean}.
 *
 * @author Carl Harris
 */
public class CredentialRequestEditorFactoryBeanTest {

  private static final long CREDENTIAL_ID = -1L;

  private static final X500Principal SUBJECT = 
      new X500Principal("cn=someSubject");

  private static final String OWNER = "owner";

  private static final String NAME = "name";

  private static final String NOTE = "note";
  
  private static final String TAG_TEXT = "tag";
  
  private static final Tag TAG = new TagEntity(TAG_TEXT);

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<ConfigurableRequestEditor> configurableEditors;

  @Mock
  private Instance<DelegatingRequestEditor> delegatingEditors;

  @Mock
  private ConfigurableRequestEditor configurableEditor;

  @Mock
  private DelegatingRequestEditor delegatingEditor;

  @Mock
  private Credential credential;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private CredentialCertificate certificate;
  
  @Mock
  private UserGroup owner;
  
  private CredentialRequestEditorFactoryBean factory = 
      new CredentialRequestEditorFactoryBean();
  
  @Before
  public void setUp() throws Exception {
    factory.configurableEditors = configurableEditors;
    factory.delegatingEditors = delegatingEditors;
  }
  
  @Test
  public void testNewEditor() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(configurableEditors).get();
      will(returnValue(configurableEditor));
      oneOf(configurableEditor).setOwner(with(UserGroup.SELF_GROUP_NAME));
    } });
    
    assertThat(factory.newEditor(),
        is(sameInstance((Object) configurableEditor)));
  }

  @Test
  public void testNewEditorWithCredential() throws Exception {
    context.checking(editorWithCredentialExpectations());
    context.checking(credentialExpectations());
    assertThat(factory.newEditor(credential), 
        is(sameInstance((Object) configurableEditor)));
  }
  
  private Expectations editorWithCredentialExpectations() throws Exception {
    return new Expectations() { {
      oneOf(configurableEditors).get();
      will(returnValue(configurableEditor));
      oneOf(configurableEditor).setCredentialId(with(CREDENTIAL_ID));
      oneOf(configurableEditor).setName(with(NAME));
      oneOf(configurableEditor).setSubjectName(with(SUBJECT));
      oneOf(configurableEditor).setOwner(with(OWNER));
      oneOf(configurableEditor).setNote(with(NOTE));
      oneOf(configurableEditor).setTags((Set<? extends Tag>) with(contains(TAG)));
    } };
  }
  
  private Expectations credentialExpectations() throws Exception {
    return new Expectations() { {
      allowing(credential).getId();
      will(returnValue(CREDENTIAL_ID));
      allowing(credential).getName();
      will(returnValue(NAME));
      allowing(credential).getOwner();
      will(returnValue(owner));
      allowing(credential).getNote();
      will(returnValue(NOTE));
      allowing(credential).getTags();
      will(returnValue(Collections.singleton(TAG)));
      allowing(credential).getCertificates();
      will(returnValue(Collections.singletonList(certificate)));
      allowing(certificate).getSubject();
      will(returnValue(SUBJECT.toString()));
      allowing(owner).getName();
      will(returnValue(OWNER));
    } };
  }
  
  @Test
  public void testNewEditorWithRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(delegatingEditors).get();
      will(returnValue(delegatingEditor));
      oneOf(delegatingEditor).setDelegate(with(same(request)));
    } });
    
    assertThat(factory.newEditor(request), 
        is(sameInstance((Object) delegatingEditor)));
  }

}

