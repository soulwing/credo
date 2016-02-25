/*
 * File created on Apr 17, 2014 
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

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 * A {@link TimerServiceManager} implemented as a singleton EJB.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TimerServiceManagerBean implements TimerServiceManager {
  
  @Resource
  protected TimerService timerService;

  /**
   * {@inheritDoc}
   */
  @Override
  public Timer createTimer(Long duration, TimerDelegate callback) {
    return timerService.createTimer(duration, callback);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean cancel(TimerDelegate delegate) {
    boolean found = false;
    for (Timer timer : timerService.getTimers()) {
      if (timer.getInfo() == delegate) {
        try {
          timer.cancel();
          found = true;
        }
        catch (NoSuchObjectLocalException ex) {         
          // already canceled
          assert true;
        }
      }
    }
    return found;
  }

  @Timeout
  public void timeout(Timer timer) {
    ((TimerDelegate) timer.getInfo()).timeout(timer);
  }
  
}
