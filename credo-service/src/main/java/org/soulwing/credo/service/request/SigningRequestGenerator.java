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

import org.soulwing.credo.SigningRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.SigningRequestEditor;
import org.soulwing.credo.service.SigningRequestException;
import org.soulwing.credo.service.UserAccessException;

/**
 * A generator that produces a {@link SigningRequest} from the contents of
 * an editor.
 *
 * @author Carl Harris
 */
public interface SigningRequestGenerator {

  /**
   * Generates a signing request.
   * @param editor editor the provides the desired properties of the signing
   *    request
   * @param protection protection parameters
   * @param errors an errors object that will be updated to report errors
   *    that occur during the generation process
   * @return signing request
   * @throws NoSuchGroupException
   * @throws GroupAccessException
   * @throws UserAccessException
   * @throws SigningRequestException if an error occurs in generating the
   *    signing request
   */
  SigningRequest generate(SigningRequestEditor editor, 
      ProtectionParameters protection, Errors errors) 
      throws NoSuchGroupException, GroupAccessException, UserAccessException,
      SigningRequestException;

}
