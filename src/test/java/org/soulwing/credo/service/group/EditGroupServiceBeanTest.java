/*
 * File created on Apr 15, 2014 
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
package org.soulwing.credo.service.group;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;

/**
 * Unit tests for {@link EditGroupServiceBean}.
 *
 * @author Carl Harris
 */
public class EditGroupServiceBeanTest {

  private static final Long GROUP_ID = -1L;
  
  private static final String GROUP_NAME = "someGroup";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupEditorFactory editorFactory;
  
  @Mock
  private ConfigurableGroupEditor editor;
  
  @Mock
  private Errors errors;
  
  private EditGroupServiceBean service = new EditGroupServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.editorFactory = editorFactory;
  }
  
  @Test
  public void testEditGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editorFactory).newEditor(with(GROUP_ID));
      will(returnValue(editor));
    } });
    
    assertThat(service.editGroup(GROUP_ID), 
        is(sameInstance((GroupEditor) editor)));
  }
  
  @Test
  public void testSaveGroup() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
    } });
    
    service.saveGroup(editor, errors);
  }

  @Test(expected = GroupAccessException.class)
  public void testSaveGroupWhenGroupAccessDenied() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    service.saveGroup(editor, errors);
  }
  
  @Test(expected = MergeConflictException.class)
  public void testSaveGroupWhenMergeConflictException() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(editor).save(with(same(errors)));
      will(throwException(new MergeConflictException()));
    } });
    
    service.saveGroup(editor, errors);
  }
  

}
