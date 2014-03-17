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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.GroupDetail;
import org.soulwing.credo.service.GroupService;

/**
 * An endpoint that provides a user's group memberships.
 *
 * @author Carl Harris
 */
@Path("/groupMemberships")
public class GroupMembershipsEndpoint {

  @Inject
  protected GroupService groupService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> getMemberships() {
    Collection<GroupDetail> groups = groupService.findAllGroups();
    List<String> memberships = new ArrayList<>();
    memberships.add(UserGroup.SELF_GROUP_NAME);
    for (GroupDetail group : groups) {
      memberships.add(group.getName());
    }
    return memberships;
  }

}
