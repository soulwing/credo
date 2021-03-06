/*
 * File created on Mar 10, 2014 
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

import javax.enterprise.context.ApplicationScoped;

/**
 * A simple bean that mocks a real {@link UserContextService}.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class MockUserContextService implements UserContextService {

  private String loginName;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    return loginName;
  }

  /**
   * Sets the login name. 
   * @param loginName the login name to set
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

}
