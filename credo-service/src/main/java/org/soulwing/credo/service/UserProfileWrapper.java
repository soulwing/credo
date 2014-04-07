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

import org.soulwing.credo.UserProfile;

/**
 * A {@link UserDetail} that wraps a {@link UserProfile}. 
 *
 * @author Carl Harris
 */
class UserProfileWrapper implements UserDetail, Serializable {

  private static final long serialVersionUID = -1089401072011563554L;

  private final UserProfile delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public UserProfileWrapper(UserProfile delegate) {
    this.delegate = delegate;
  }

  @Override
  public Long getId() {
    return delegate.getId();
  }

  @Override
  public String getLoginName() {
    return delegate.getLoginName();
  }

  @Override
  public String getFullName() {
    return delegate.getFullName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof UserProfileWrapper)) return false;
    return delegate.equals(((UserProfileWrapper) obj).delegate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s (%s)", getFullName(), getLoginName());
  }
  
}