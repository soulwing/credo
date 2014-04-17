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

import java.io.Serializable;

import javax.ejb.Timer;

/**
 * A delegate for a timer callback.
 *
 * @author Carl Harris
 */
public interface TimerDelegate extends Serializable {

  /**
   * Notifies the recipient that the timer has expired.
   * @param timer the subject timer
   */
  void timeout(Timer timer);
  
}
