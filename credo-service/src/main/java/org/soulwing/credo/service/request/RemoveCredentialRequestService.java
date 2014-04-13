/*
 * File created on Apr 12, 2014 
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
package org.soulwing.credo.service.request;

import javax.ejb.Local;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;

/**
 * A service that provides support for removing {@link CredentialRequest}
 * objects via a user interaction.
 *
 * @author Carl Harris
 */
@Local
public interface RemoveCredentialRequestService {

  /**
   * Finds a request using its unique identifier.
   * @param id unique identifier of the subject request
   * @return request
   * @throws NoSuchCredentialException if no request exists with the given
   *    identifier
   */
  CredentialRequestDetail findRequestById(Long id) 
      throws NoSuchCredentialException;
  
  /**
   * Removes a request.
   * @param id unique identifier of the request to remove
   * @throws GroupAccessException if the logged-in user is not a member of
   *    the group that owns the given request
   */
  void removeRequest(Long id) throws GroupAccessException;
  
}
