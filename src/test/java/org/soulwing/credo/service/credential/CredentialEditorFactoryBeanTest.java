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
package org.soulwing.credo.service.credential;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.service.request.CredentialRequestEditorFactoryBean;

/**
 * Unit tests for {@link CredentialRequestEditorFactoryBean}.
 *
 * @author Carl Harris
 */
public class CredentialEditorFactoryBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<DelegatingCredentialEditor> editors;

  @Mock
  private DelegatingCredentialEditor editor;

  @Mock
  private Credential credential;
  
  private CredentialEditorFactoryBean factory = 
      new CredentialEditorFactoryBean();
  
  @Before
  public void setUp() throws Exception {
    factory.editors = editors;
  }

  @Test
  public void testNewEditor() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editors).get();
      will(returnValue(editor));
      oneOf(editor).setDelegate(with(same(credential)));
    } });
    
    assertThat(factory.newEditor(credential), 
        is(sameInstance((Object) editor)));
  }

}

