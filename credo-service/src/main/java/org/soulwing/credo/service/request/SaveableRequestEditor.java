/*
 * File created on Apr 13, 2014 
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

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;


/**
 * A credential request editor with a save method.
 *
 * @author Carl Harris
 */
public interface SaveableRequestEditor extends CredentialRequestEditor {

  /**
   * Saves the contents of the editor to the target request, creating or
   * updating the underlying request entity as needed.
   * @param protection protection parameters
   * @param errors an errors object that will be updated if a recoverable
   *    error occurs
   * @throws CredentialRequestException if a recoverable error occurs in 
   *    applying the editor to the target request
   * @throws NoSuchCredentialException if the request was removed after the
   *    editor was created
   * @throws GroupAccessException if the logged in user is not a member
   *    of the group that owns the credential
   * @throws PassphraseException if a password is required and was not
   *    provided or was incorrect
   * @throws MergeConflictException if the persistent state of the request
   *    has changed since the editor was created
   */
  void save(ProtectionParameters protection, 
      Errors errors) throws CredentialRequestException,
      NoSuchCredentialException, GroupAccessException, PassphraseException, 
      MergeConflictException;
  
}
