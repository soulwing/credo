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
package org.soulwing.credo.facelets;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.soulwing.credo.service.UserContextService;

/**
 * A bean that provides the logged in user name.
 *
 * @author Carl Harris
 */
@SessionScoped
public class UserContextBean implements UserContextService, Serializable {

  private static final long serialVersionUID = -3825612074731463595L;

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  
  @Inject
  protected FacesContext facesContext;
  
  private String loginName;
    
  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    String loginName = getCachedLoginName();
    if (loginName == null) {
      loginName = getRemoteLoginName();
    }
    if (loginName == null) {
      throw new IllegalArgumentException("no user is logged in");
    }
    return loginName;
  }

  private String getRemoteLoginName() {
    lock.writeLock().lock();
    try {
      // check again while we're holding the write lock
      if (loginName == null) {
        loginName = facesContext.getExternalContext().getRemoteUser();
      }
      return loginName;
    }
    finally {
      lock.writeLock().unlock();        
    }
  }

  private String getCachedLoginName() {
    lock.readLock().lock();
    try {
      return loginName;
    }
    finally {
      lock.readLock().unlock();
    }
  }
  
}
