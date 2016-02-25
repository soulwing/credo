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
package org.soulwing.credo.filters;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.SessionScoped;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.MutableUserContextService;

/**
 * A bean that provides the logged in user name.
 *
 * @author Carl Harris
 */
@SessionScoped
public class UserContextBean implements MutableUserContextService, Serializable {

  private static final long serialVersionUID = -3825612074731463595L;

  private final Lock lock = new ReentrantLock();
  
  private String loginName;
    
  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    if (loginName == null) {
      loginName = getRemoteLoginName();
    }
    if (loginName == null) {
      throw new IllegalStateException("no user is logged in");
    }
    return loginName;
  }

  private String getRemoteLoginName() {
    lock.lock();
    try {
      return loginName;
    }
    finally {
      lock.unlock();        
    }
  }

  @Override
  public void setLoginName(String loginName) {
    Validate.notNull(loginName, "loginName must not be null");
    lock.lock();
    try {
      if (this.loginName != null && !this.loginName.equals(loginName)) {
        throw new IllegalArgumentException(
            "login name cannot be changed in same session");
      }
      this.loginName = loginName;
    }
    finally {
      lock.unlock();
    }
  }

}
