/*
 * File created on Apr 14, 2014 
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

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.request.CredentialRequestException;

/**
 * A {@link CredentialEditor} with a save method.
 *
 * @author Carl Harris
 */
public interface SaveableCredentialEditor extends CredentialEditor {

  /**
   * Saves the contents of the editor to the target credential, creating or
   * updating the underlying credential entity as needed.
   * @param errors an errors object that will be updated if a recoverable
   *    error occurs
   * @throws CredentialRequestException if a recoverable error occurs in 
   *    applying the editor to the target credential
   * @throws NoSuchCredentialException if the credential was removed after the
   *    editor was created
   * @throws GroupAccessException if the logged in user is not a member
   *    of the group that owns the credential
   * @throws PassphraseException if a password is required and was not
   *    provided or was incorrect
   * @throws MergeConflictException if the persistent state of the credential
   *    has changed since the editor was created
   */
  void save(Errors errors) throws CredentialException,
      NoSuchCredentialException, GroupAccessException, PassphraseException, 
      MergeConflictException;

}
