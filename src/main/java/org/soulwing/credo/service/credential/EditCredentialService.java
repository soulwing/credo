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
package org.soulwing.credo.service.credential;

import javax.ejb.Local;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.group.NoSuchGroupException;

/**
 * A service that provides support for editing {@link CredentialRequest}
 * objects via a user interaction.
 *
 * @author Carl Harris
 */
@Local
public interface EditCredentialService {

  /**
   * Creates an editor for the given credential.
   * @param id unique identifier for the credential
   * @return editor instance
   * @throws NoSuchGroupException if the specified credential does not exist
   *    (or is not visible to the logged in user)
   */
  CredentialEditor editCredential(Long id) 
      throws NoSuchCredentialException;
  
  /**
   * Applies the changes in the given editor to the credential it represents, 
   * effectively making the requested changes persistent.
   * @param editor the editor to apply
   * @param errors an errors object that will be updated if the edits cannot
   *    be successfully applied
   * @throws CredentialException if a recoverable error occurs in 
   *    applying the editor to the target credential
   * @throws NoSuchCredentialException if the credential was removed after the
   *    editor was created
   * @throws PassphraseException if a password is required and was not
   *    provided or was incorrect
   * @throws GroupAccessException if the logged in user is not a member
   *    of the group that owns the credential
   * @throws MergeConflictException if the persistent state of the credential
   *    has changed since the editor was created
   */
  void saveCredential(CredentialEditor editor, Errors errors) 
      throws CredentialException, NoSuchCredentialException, 
      GroupAccessException, PassphraseException, MergeConflictException;

}
