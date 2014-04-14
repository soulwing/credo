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
package org.soulwing.credo.service.request;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;

/**
 * A generator that produces a {@link CredentialRequest} from the contents of
 * an editor.
 *
 * @author Carl Harris
 */
public interface CredentialRequestGenerator {

  /**
   * Generates a signing request.
   * @param editor editor the provides the desired properties of the signing
   *    request
   * @param protection protection parameters
   * @param errors TODO
   * @return signing request
   * @throws NoSuchGroupException if the group specified in {@code protection}
   *    does not exist
   * @throws GroupAccessException if the logged-in user is not a member of
   *    group specified in {@code protection} 
   * @throws UserAccessException if the logged-in user's profile could not be
   *    accessed using the password specified in {@code protection}
   * @throws CredentialRequestException if an error occurs in generating the
   *    signing request
   */
  CredentialRequest generate(CredentialRequestEditor editor, 
      ProtectionParameters protection, Errors errors) 
      throws NoSuchGroupException, GroupAccessException, UserAccessException,
      CredentialRequestException;

}
