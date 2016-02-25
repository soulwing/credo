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

import java.security.PrivateKey;

/**
 * A simple cache for a user's private key.
 * <p>
 * An implementation is typically a session-scoped bean that holds the
 * user's private key in memory for a short period of time to avoid requiring
 * the user to repeatedly when multiple operations are performed that 
 * require the key.
 * <p>
 * Any implementation of this interface must be thread safe, such that 
 * multiple concurrent requests to either the get or set methods are
 * properly synchronized.
 * 
 * @author Carl Harris
 */
public interface PrivateKeyHolder {

  /**
   * Gets the cached private key, if any.
   * @return private key or {@code null} if the key has not been set
   */
  PrivateKey getPrivateKey();
  
  /**
   * Sets the private key to hold in cache.
   * @param privateKey the private key to set
   */
  void setPrivateKey(PrivateKey privateKey);
  
}
