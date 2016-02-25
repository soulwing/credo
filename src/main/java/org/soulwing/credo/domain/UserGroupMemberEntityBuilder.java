/*
 * File created on Mar 3, 2014 
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
package org.soulwing.credo.domain;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserGroupMemberBuilder;
import org.soulwing.credo.UserProfile;

/**
 * A {@link UserGroupMemberBuilder} that builds a
 * {@link UserGroupMemberEntity}.
 *
 * @author Carl Harris
 */
public class UserGroupMemberEntityBuilder implements UserGroupMemberBuilder {

  private final UserGroupMemberEntity entity = 
      new UserGroupMemberEntity();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroupMemberBuilder setUser(UserProfile user) {
    if (!(user instanceof UserProfileEntity)) {
      throw new IllegalArgumentException("unrecognized user type: "
          + user.getClass().getName());
    }
    entity.setUser((UserProfileEntity) user);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroupMemberBuilder setGroup(UserGroup group) {
    if (!(group instanceof UserGroupEntity)) {
      throw new IllegalArgumentException("unrecognized group type: "
          + group.getClass().getName());
    }
    entity.setGroup((UserGroupEntity) group);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroupMemberBuilder setSecretKey(String secretKey) {
    entity.setSecretKey(secretKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroupMember build() {
    return entity;
  }

}
