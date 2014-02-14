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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.servlet.http.Part;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportService;

/**
 * Unit tests for {@link AddCredentialBean}.
 *
 * @author Carl Harris
 */
public class AddCredentialBeanTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  public Conversation conversation;
  
  @Mock
  public Errors errors;
  
  @Mock
  public ImportService importService;
    
  @Mock
  public Credential credential;
  
  private AddCredentialBean bean = new AddCredentialBean();

  @Before
  public void setUp() throws Exception {
    bean.conversation = conversation;
    bean.errors = errors;
    bean.importService = importService;
  }

  @Test
  public void testUploadWithNoFilesSelected() throws Exception {
    context.checking(conversationExpectations());
    context.checking(new Expectations() { { 
      oneOf(errors).addError( 
          with(AddCredentialBean.FILE_REQUIRED_MESSAGE),
          with(emptyArray()));
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
    context.checking(uploadSuccessExpectations(file, false));
    
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }

  @Test
  public void testUploadSuccessWithFile1Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, false));
    
    bean.setFile1(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }
    
  @Test
  public void testUploadSuccessWithFile2Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, false));
    
    bean.setFile2(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadError() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));
      will(throwException(new ImportException()));
    } });
    
    bean.setFile0(file);
    assertThat(bean.upload(), nullValue());
  }

  @Test
  public void testUploadWarning() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(conversationExpectations());
    context.checking(uploadSuccessExpectations(file, true));
    
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.WARNINGS_OUTCOME_ID));
    assertThat(bean.getCredential(), sameInstance(credential));
  }

  @SuppressWarnings("unchecked")
  private Expectations uploadSuccessExpectations(final Part file,
      final boolean hasWarnings) 
      throws Exception {
    return new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));    
      will(returnValue(credential));
      oneOf(errors).hasWarnings();
      will(returnValue(hasWarnings));
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
    assertThat(bean.save(), equalTo(AddCredentialBean.SUCCESS_OUTCOME_ID));    
  }

  @Test
  public void testSaveError() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importService).saveCredential(with(same(credential)), 
          with(same(errors)));
      will(throwException(new ImportException()));
    } });
    
    bean.setCredential(credential);
    assertThat(bean.save(), nullValue());    
  }
  

}
