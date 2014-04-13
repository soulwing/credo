/*
 * File created on Apr 13, 2014 
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.NoSuchCredentialException;

/**
 * Unit tests for {@link EditCredentialRequestBean}.
 *
 * @author Carl Harris
 */
public class EditCredentialRequestServiceBeanTest {

  private static final Long REQUEST_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRequestRepository requestRepository;
  
  @Mock
  private CredentialRequestEditorFactory editorFactory;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private SaveableRequestEditor editor;
  
  @Mock
  private Errors errors;
  
  private EditCredentialRequestServiceBean service = 
      new EditCredentialRequestServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.requestRepository = requestRepository;
    service.editorFactory = editorFactory;
  }
  
  @Test
  public void testEditRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(editorFactory).newEditor(with(same(request)));
      will(returnValue(editor));
    } });
    
    assertThat(service.editRequest(REQUEST_ID), 
        is(sameInstance((Object) editor)));
  }
  
  @Test(expected = NoSuchCredentialException.class)
  public void testEditRequestWhenRequestNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(null));
    } });
    
    service.editRequest(REQUEST_ID);
  }
  
  @Test
  public void testSaveRequest() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
    } });

    service.saveRequest(editor, errors);
  }

}
