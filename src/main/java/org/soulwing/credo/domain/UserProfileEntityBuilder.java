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

import org.soulwing.credo.UserProfile;
import org.soulwing.credo.UserProfileBuilder;

/**
 * A {@link UserProfileBuilder} that builds a {@link UserProfileEntity}.
 *
 * @author Carl Harris
 */
public class UserProfileEntityBuilder implements UserProfileBuilder {

  private final UserProfileEntity entity = new UserProfileEntity();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfileBuilder setLoginName(String loginName) {
    entity.setLoginName(loginName);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfileBuilder setFullName(String fullName) {
    entity.setFullName(fullName);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfileBuilder setPassword(String password) {
    entity.setPassword(password);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfileBuilder setPublicKey(String publicKey) {
    entity.setPublicKey(publicKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfileBuilder setPrivateKey(String privateKey) {
    entity.setPrivateKey(privateKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile build() {
    return entity;
  }

}
