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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import javax.servlet.http.Part;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
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
  public Errors errors;
  
  @Mock
  public ImportService importService;
    
  @Mock
  public Credential credential;
  
  private AddCredentialBean bean = new AddCredentialBean();

  @Before
  public void setUp() throws Exception {
    bean.errors = errors;
    bean.importService = importService;
  }
  
  @Test
  public void testUploadWithNoFilesSelected() throws Exception {
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
  @SuppressWarnings("unchecked")
  public void testUploadSuccessWithFile0Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));
      will(returnValue(credential));
      oneOf(errors).hasWarnings();
      will(returnValue(false));
    } });
    
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadSuccessWithFile1Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));
      will(returnValue(credential));
      oneOf(errors).hasWarnings();
      will(returnValue(false));
    } });
    
    bean.setFile1(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
  }
    
  @Test
  @SuppressWarnings("unchecked")
  public void testUploadSuccessWithFile2Selected() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));
      will(returnValue(credential));
      oneOf(errors).hasWarnings();
      will(returnValue(false));
    } });
    
    bean.setFile2(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.DETAILS_OUTCOME_ID));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUploadError() throws Exception {
    final Part file = context.mock(Part.class);
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
  @SuppressWarnings("unchecked")
  public void testUploadWarning() throws Exception {
    final Part file = context.mock(Part.class);
    context.checking(new Expectations() { {
      oneOf(importService).importCredential(
          (List<FileContentModel>) with(contains(new PartContent(file))), 
          with(errors));
      oneOf(errors).hasWarnings();
      will(returnValue(true));
    } });
    
    bean.setFile0(file);
    assertThat(bean.upload(), equalTo(AddCredentialBean.WARNINGS_OUTCOME_ID));
  }

}
