/*
 * File created on Mar 17, 2014 
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
package org.soulwing.credo.endpoints;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.group.GroupDetail;
import org.soulwing.credo.service.group.GroupService;

/**
 * Unit tests for {@link TagsEndpoint}.
 *
 * @author Carl Harris
 */
public class GroupMembershipsEndpointTest {

  private static final String GROUP_NAME = "groupName";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private GroupService groupService;
  
  @Mock
  private GroupDetail group;
  
  private GroupMembershipsEndpoint endpoint = new GroupMembershipsEndpoint();
  
  @Before
  public void setUp() throws Exception {
    endpoint.groupService = groupService;
  }
  
  @Test
  public void testGetMemberships() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(groupService).findAllGroups();
      will(returnValue(Collections.singleton(group)));
      oneOf(group).getName();
      will(returnValue(GROUP_NAME));
    } });
    
    assertThat(endpoint.getMemberships(), contains(UserGroup.SELF_GROUP_NAME, 
        GROUP_NAME));
  }

}
