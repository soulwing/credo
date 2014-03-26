/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.repository;

import java.util.List;

import org.soulwing.credo.CredentialRequest;

/**
 * A repository of {@link CredentialRequest} objects.
 *
 * @author Carl Harris
 */
public interface CredentialRequestRepository {

  /**
   * Adds a (transient) request to the repository, making it persistent.
   * @param request the request to add
   */
  void add(CredentialRequest request);
  
  /**
   * Updates (merges) a possibly detached request, making changes to it
   * persistent.
   * @param request the request to update
   * @return the updated request
   */
  CredentialRequest update(CredentialRequest request);
  
  /**
   * Removes a persistent request from the repository.
   * @param request the request to remove
   * @param removePrivateKey flag indicating whether the request's 
   *    private key should also be removed
   */
  void remove(CredentialRequest request, boolean removePrivateKey);
  
  /**
   * Finds a request using its unique identifier.
   * @param id unique ID of the request to match
   * @return request or {@code null} if no such request exists
   */
  CredentialRequest findById(Long id);
  
  /**
   * Finds all credential requests in the repository that are accessible to 
   * the given user.
   * @param loginName login name of the subject user 
   * @return list of requests
   */
  List<CredentialRequest> findAllByLoginName(String loginName);
  

}
