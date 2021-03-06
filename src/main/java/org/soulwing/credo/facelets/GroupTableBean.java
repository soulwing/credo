/*
 * File created on Feb 21, 2014 
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

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.group.GroupDetail;
import org.soulwing.credo.service.group.GroupService;

/**
 * A bean that supports a view containing a table of groups.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class GroupTableBean {

  @Inject
  protected GroupService groupService;

  private Collection<GroupDetail> groups;
 
  
  /**
   * Gets the collection of groups to display in the table
   * @return groups
   */
  public Collection<GroupDetail> getGroups() {
    if (groups == null) {
      groups = groupService.findAllGroups();
    }
    return groups;
  }
  
}
