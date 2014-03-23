/*
 * File created on Mar 18, 2014 
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

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.ValueChangeEvent;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.facelets.DelegatingCredentialEditor.OwnerStatus;
import org.soulwing.credo.service.CredentialEditor;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.GroupService;

/**
 * Unit tests for {@link DelegatingCredentialEditor}.
 *
 * @author Carl Harris
 */
public class DelegatingCredentialEditorTest {

  private static final String GROUP_NAME = "groupName";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private PartialViewContext partialViewContext;
  
  @Mock
  private UIViewRoot viewRoot;
  
  @Mock
  private ValueChangeEvent event;
  
  private DelegatingCredentialEditor<CredentialEditor> bean = 
      new DelegatingCredentialEditor<CredentialEditor>();
  
  @Before
  public void setUp() throws Exception {
    bean.groupService = groupService;
    bean.facesContext = facesContext;
  }
  
  @Test
  public void testOwnerChangedToBlank() throws Exception {
    context.checking(resetComponentsExpectations());
    context.checking(new Expectations() { { 
      oneOf(event).getNewValue();
      will(returnValue(""));
    } });
    
    bean.ownerChanged(event);
    assertThat(bean.getOwnerStatus(), is(equalTo(OwnerStatus.NONE)));
  }

  @Test
  public void testOwnerChangedWhenGroupExists() throws Exception {
    context.checking(resetComponentsExpectations());
    context.checking(new Expectations() { { 
      oneOf(event).getNewValue();
      will(returnValue(GROUP_NAME));
      oneOf(groupService).isExistingGroup(with(GROUP_NAME));
      will(returnValue(true));
    } });
    
    bean.ownerChanged(event);
    assertThat(bean.getOwnerStatus(), is(equalTo(OwnerStatus.EXISTS)));
  }

  @Test
  public void testOwnerChangedWhenGroupDoesNotExist() throws Exception {
    context.checking(resetComponentsExpectations());
    context.checking(new Expectations() { {
      oneOf(event).getNewValue();
      will(returnValue(GROUP_NAME));
      oneOf(groupService).isExistingGroup(with(GROUP_NAME));
      will(returnValue(false));
    } });
    
    bean.ownerChanged(event);
    assertThat(bean.getOwnerStatus(), is(equalTo(OwnerStatus.WILL_CREATE)));
  }

  @Test
  public void testOwnerChangedWhenGroupAccessDenied() throws Exception {
    context.checking(resetComponentsExpectations());
    context.checking(new Expectations() { {       
      oneOf(event).getNewValue();
      will(returnValue(GROUP_NAME));
      oneOf(groupService).isExistingGroup(with(GROUP_NAME));
      will(throwException(new GroupAccessException(GROUP_NAME)));
    } });
    
    bean.ownerChanged(event);
    assertThat(bean.getOwnerStatus(), is(equalTo(OwnerStatus.INACCESSIBLE)));
  }

  private Expectations resetComponentsExpectations() throws Exception { 
    return new Expectations() { { 
      oneOf(facesContext).getPartialViewContext();
      returnValue(partialViewContext);
      oneOf(facesContext).getViewRoot();
      returnValue(viewRoot);
    } };
  }

}
