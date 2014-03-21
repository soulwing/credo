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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.TagEntity;
import org.soulwing.credo.service.SigningRequestEditor;

/**
 * Unit tests for {@link ConcreteRequestEditorFactory}.
 *
 * @author Carl Harris
 */
public class ConcreteRequestEditorFactoryTest {

  private static final String SUBJECT = "subject";

  private static final String OWNER = "owner";

  private static final String NAME = "name";

  private static final String NOTE = "note";
  
  private static final String TAG_TEXT = "tag";
  
  private static final Tag TAG = new TagEntity(TAG_TEXT);

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<ConfigurableRequestEditor> editorInstance;
  
  @Mock
  private ConfigurableRequestEditor editor;
  
  @Mock
  private Credential credential;
  
  @Mock
  private CredentialCertificate certificate;
  
  @Mock
  private UserGroup owner;
  
  private ConcreteRequestEditorFactory factory = 
      new ConcreteRequestEditorFactory();
  
  @Before
  public void setUp() throws Exception {
    factory.editorInstance = editorInstance;
  }

  @Test
  public void testNewEditorWithCredential() throws Exception {
    context.checking(editorExpectations());
    context.checking(credentialExpectations());
    assertThat(factory.newEditor(credential), 
        is(sameInstance((SigningRequestEditor) editor)));
  }
  
  private Expectations editorExpectations() throws Exception {
    return new Expectations() { {
      oneOf(editorInstance).get();
      will(returnValue(editor));
      oneOf(editor).setName(with(NAME));
      oneOf(editor).setSubjectName(with(SUBJECT));
      oneOf(editor).setOwner(with(OWNER));
      oneOf(editor).setNote(with(NOTE));
      oneOf(editor).setTags((Set<? extends Tag>) with(contains(TAG)));
    } };
  }
  
  private Expectations credentialExpectations() throws Exception {
    return new Expectations() { { 
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
      will(returnValue(SUBJECT));
      allowing(owner).getName();
      will(returnValue(OWNER));
    } };
  }
  
}

