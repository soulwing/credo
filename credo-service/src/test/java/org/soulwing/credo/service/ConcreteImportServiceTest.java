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
import static org.hamcrest.Matchers.sameInstance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.TagRepository;

/**
 * Unit tests for {@link ConcreteImportService}.
 *
 * @author Carl Harris
 */
public class ConcreteImportServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialBuilderFactory factory;
  
  @Mock
  private CredentialBuilder builder;
  
  @Mock
  private Credential credential;
  
  @Mock
  private Errors errors;
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private TagRepository tagRepository;
  
  public ConcreteImportService importService = new ConcreteImportService();
  
  @Before
  public void setUp() throws Exception {
    importService.credentialBuilderFactory = factory;
    importService.credentialRepository = credentialRepository;
    importService.tagRepository = tagRepository;
  }
  
  @Test(expected = ImportException.class)
  public void testPrepareImportWithNoFiles() throws Exception {
    context.checking(new Expectations() { {
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
      oneOf(factory).newInstance();
      will(returnValue(builder));
      oneOf(file).getInputStream();
      will(returnValue(inputStream));
      oneOf(file).getName();
      will(returnValue(name));
      oneOf(builder).loadFile(with(same(inputStream)));
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
      oneOf(factory).newInstance();
      will(returnValue(builder));
      oneOf(file).getInputStream();
      will(returnValue(inputStream));
      oneOf(file).getName();
      will(returnValue(name));
      oneOf(builder).loadFile(with(same(inputStream)));
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
      oneOf(builder).validate(with(same(errors)));
      will(throwException(new ImportException()));
    } });    
    
    importService.createCredential(builder, errors);
  }

  @Test
  public void testCreateCredentialWhenSuccessful() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(builder).validate(with(same(errors)));
      oneOf(builder).build();
      will(returnValue(credential));
    } });    
    
    assertThat(importService.createCredential(builder, errors), 
        sameInstance(credential));    
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
      oneOf(tagRepository).newInstance(with(same(token)));
      will(returnValue(tag));
    } });
    
    assertThat(importService.resolveTags(new String[] { token }),
        contains(tag));
  }

}
