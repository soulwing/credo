/*
 * File created on Feb 21, 2014 
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
package org.soulwing.credo.service.credential;

import java.util.List;

import javax.ejb.Local;

import org.soulwing.credo.Credential;
import org.soulwing.credo.service.GroupAccessException;

/**
 * A service that provides access to the credentials managed by the 
 * application.
 *
 * @author Carl Harris
 */
@Local
public interface CredentialService {

  /**
   * Finds a credential using its persistent identifier.
   * @param id identifier of the subject credential
   * @return credential
   * @throws NoSuchCredentialException if no credential exists with the
   *    given identifier
   */
  Credential findCredentialById(Long id) throws NoSuchCredentialException;
  
  /**
   * Finds all credentials accessible to the given user.
   * @return list of credentials
   */
  List<Credential> findAllCredentials();
  
  /**
   * Removes a credential.
   * <p>
   * This method does not throw an exception if the credential to remove no
   * longer exists.
   * 
   * @param id unique identifier of the credential to remove
   * @param errors errors object that will be updated in the case of an
   *    error
   * @throws GroupAccessException if the logged in user is not a member of
   *    the group that owns the given credential
   */
  void removeCredential(Long id) throws GroupAccessException;

}
