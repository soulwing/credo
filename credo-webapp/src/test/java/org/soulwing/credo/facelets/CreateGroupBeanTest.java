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
package org.soulwing.credo.facelets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

/**
 * Unit tests for {@link CreateGroupBean}.
 *
 * @author Carl Harris
 */
public class CreateGroupBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private GroupEditor editor;
  
  @Mock
  private Errors errors;
  
  private CreateGroupBean bean = new CreateGroupBean();
  
  @Before
  public void setUp() throws Exception {
    bean.groupService = groupService;
    bean.errors = errors;
    bean.editor = new DelegatingGroupEditor();
    bean.editor.setDelegate(this.editor);
  }
  
  @Test
  public void testInit() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).newGroup();
      will(returnValue(editor));
    } });
    
    bean.init();
    assertThat(bean.getEditor().getDelegate(), is(sameInstance(editor)));
  }
  
  @Test
  public void testSaveSuccess() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));      
    } });
    
    assertThat(bean.save(), is(equalTo(CreateGroupBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test
  public void testSaveWhenGroupEditException() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
      will(throwException(new GroupEditException()));
    } });
    
    assertThat(bean.save(), is(nullValue()));
  }

  @Test(expected = RuntimeException.class)
  public void testSaveWhenNoSuchGroupException() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
      will(throwException(new NoSuchGroupException()));
    } });
    
    bean.save();
  }

  @Test(expected = RuntimeException.class)
  public void testSaveWhenPassphraseException() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).saveGroup(with(same(editor)), with(same(errors)));
      will(throwException(new PassphraseException()));
    } });
    
    bean.save();
  }

  @Test
  public void testCancel() throws Exception {
    assertThat(bean.cancel(), is(equalTo(CreateGroupBean.CANCEL_OUTCOME_ID)));
  }

}
