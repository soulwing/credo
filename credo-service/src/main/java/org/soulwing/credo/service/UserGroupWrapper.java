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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.soulwing.credo.UserGroup;

/**
 * A {@link GroupDetail} that wraps a {@link UserGroup}. 
 *
 * @author Carl Harris
 */
class UserGroupWrapper implements GroupDetail, Serializable {

  private static final long serialVersionUID = -591688369847856465L;
  
  private final UserGroup delegate;
  private final Collection<UserDetail> members = new ArrayList<>();
  private boolean inUse;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public UserGroupWrapper(UserGroup delegate) {
    this.delegate = delegate;
  }

  /**
   * Adds a member to the group represented by this wrapper.
   * @param member the member to add
   */
  public void addMember(UserDetail member) {
    this.members.add(member);
  }
  
  /** 
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return delegate.getId();
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return delegate.getName();
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return delegate.getDescription();
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Collection<UserDetail> getMembers() {
    return members;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInUse() {
    return inUse;
  }

  /**
   * Sets the flag indicating whether the group is in use.
   * @param inUse the flag state to set
   */
  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }
  
}