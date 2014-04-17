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

import javax.ejb.Timer;

/**
 * A manager for an EJB {@link TimerService}.
 * <p>
 * The {@code TimerService} is useful, but cannot be easily injected into
 * CDI beans.  An implementation of this interface will typically be a
 * singleton EJB that can be injected into CDI beans, and acts as a simple
 * proxy to the underlying timer service.
 *
 * @author Carl Harris
 */
public interface TimerServiceManager {

  /**
   * Create a single-action timer that expires after a specified duration.
   * @param duration duration in milliseconds
   * @param delegate the timer delegate that will be called back when the
   *    timer expires
   * @return scheduled timer
   */
  Timer createTimer(Long duration, TimerDelegate callback);
  
  /**
   * Cancels any existing timer scheduled to invoke the given delegate.
   * @param delegate the delegate whose timer(s) is (are) to be canceled
   * @return {@code true} if a timer was canceled
   */
  boolean cancel(TimerDelegate delegate);
  
}
