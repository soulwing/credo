/*
 * File created on Feb 16, 2014 
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

import org.soulwing.credo.Credential;

/**
 * A repository of persistent {@link Credential} objects.
 *
 * @author Carl Harris
 */
public interface CredentialRepository {

  /**
   * Adds a credential to the repository.
   * @param credential the credential to add
   */
  void add(Credential credential);

  /**
   * Updates (merges) the state of the given credential with the corresponding
   * persistent credential
   * <p>
   * @param credential the credential to update
   */
  void update(Credential credential);
  
  /**
   * Removes a credential from the repository.
   * @param id the unique identifier of the credential to remove
   */
  void remove(Long id);
  
  /**
   * Finds a credential using its persistent identifier
   * @param id identifier of the credential to retrieve
   * @return credential or {@code null} if no credential exists with the
   *    given {@code id}
   */
  Credential findById(Long id);
  
  /**
   * Finds all credentials in the repository that are accessible to the given
   * user.
   * @param loginName login name of the subject user 
   * @return list of credentials
   */
  List<Credential> findAllByLoginName(String loginName);
  
  /**
   * Finds a credential in the repository with a given owner.
   * @param ownerId unique identifier of the owner.
   * @return list of credentials
   */
  List<Credential> findAllByOwnerId(Long ownerId);
  
  /**
   * Finds a request in the repository that was created for the given request.
   * @param requestId unique identifier of the request
   * @return credential or {@code null} if no such credential exists
   */
  Credential findByRequestId(Long requestId);
  
}
