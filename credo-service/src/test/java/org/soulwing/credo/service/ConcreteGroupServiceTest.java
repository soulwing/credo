/*
 * File created on Mar 14, 2014 
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.group.GroupEditorFactory;
import org.soulwing.credo.service.group.SaveableGroupEditor;

/**
 * Unit tests for {@link ConcreteGroupService}.
 *
 * @author Carl Harris
 */
public class ConcreteGroupServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupEditorFactory editorFactory;
  
  @Mock
  private SaveableGroupEditor editor;
  
  @Mock
  private Errors errors;
  
  private ConcreteGroupService service = new ConcreteGroupService();
  
  @Before
  public void setUp() throws Exception {
    service.editorFactory = editorFactory;
  }
  
  @Test
  public void testNewEditor() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editorFactory).newEditor();
      will(returnValue(editor));
    } });
    
    assertThat(service.newGroup(), is(sameInstance((GroupEditor) editor)));
  }
  
  @Test
  public void testSaveGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
    } });
    
    service.saveGroup(editor, errors);
  }
  
}
