/*
 * File created on Apr 16, 2014 
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
package org.soulwing.credo.service.protect;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.TimerDelegate;
import org.soulwing.credo.service.TimerServiceManager;

/**
 * A {@link PrivateKeyHolder} implemented as a bean.
 * <p>
 * This implementation uses the {@link TimerService} to schedule a timer
 * that will discard the cached key after a period of inactivity.
 *
 * @author Carl Harris
 */
@SessionScoped
public class PrivateKeyHolderBean implements PrivateKeyHolder, 
    TimerDelegate, Serializable {

  private static final long serialVersionUID = 1487939620990626627L;

  static final Long TIMEOUT_DURATION = 300000L;
  
  private final Lock lock = new ReentrantLock();
  
  @Inject
  protected TimerServiceManager timerServiceManager;
  
  private PrivateKey privateKey;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey getPrivateKey() {
    lock.lock();
    try {
      scheduleTimeout();
      return privateKey;
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivateKey(PrivateKey privateKey) {
    lock.lock();
    try {
      this.privateKey = privateKey;
      scheduleTimeout();
    }
    finally {
      lock.unlock();
    }
  }
  
  /**
   * Schedules the timer that will discard the cached private key after a
   * period of inactivity.
   */
  private void scheduleTimeout() {
    timerServiceManager.cancel(this);
    if (privateKey != null) {
      timerServiceManager.createTimer(TIMEOUT_DURATION, this);
    }
  }
  
  /**
   * Notifies the recipient that the key holding timer has expired.
   * @param timer the subject timer
   */
  @Override
  public void timeout(Timer timer) {
    setPrivateKey(null);
  }
  
}
