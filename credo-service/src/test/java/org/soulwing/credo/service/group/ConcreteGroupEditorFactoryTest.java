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
package org.soulwing.credo.service.group;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collection;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.UserProfileService;

/**
 * Unit tests for {@link ConcreteGroupEditorFactory}.
 *
 * @author Carl Harris
 */
public class ConcreteGroupEditorFactoryTest {

  private static final Long OWNER_ID = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<ConfigurableGroupEditor> newGroupEditor;
  
  @Mock
  private UserProfileService profileService;
  
  @Mock
  private UserGroupRepository groupRepository;
  
  @Mock
  private ConfigurableGroupEditor editor;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private UserProfile profile;
  
  @Mock
  private Collection<UserDetail> users;
  
  private ConcreteGroupEditorFactory editorFactory =
      new ConcreteGroupEditorFactory();
  
  @Before
  public void setUp() throws Exception {
    editorFactory.newGroupEditor = newGroupEditor;
    editorFactory.profileService = profileService;
    editorFactory.groupRepository = groupRepository;   
  }

  @Test
  public void testNewEditor() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(newGroupEditor).get();
      will(returnValue(editor));
      oneOf(groupRepository).newGroup(with(""));
      will(returnValue(group));
      oneOf(profileService).findAllProfiles();
      will(returnValue(users));
      oneOf(profileService).getLoggedInUserProfile();
      will(returnValue(profile));
      oneOf(profile).getId();
      will(returnValue(OWNER_ID));
      oneOf(editor).setGroup(with(same(group)));
      oneOf(editor).setOwner(with(OWNER_ID));
      oneOf(editor).setMembership(with(arrayContaining(OWNER_ID)));
      oneOf(editor).setUsers(with(same(users)));
    } });
    
    assertThat(editorFactory.newEditor(), is(sameInstance(editor)));
  }
}

