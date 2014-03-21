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

import org.soulwing.credo.Credential;
import org.soulwing.credo.service.CredentialEditor;
import org.soulwing.credo.service.CredentialRequestEditor;

/**
 * A factory that produces {@link CredentialEditor} objects for 
 *  requests.
 *
 * @author Carl Harris
 */
public interface CredentialRequestEditorFactory {

  /**
   * Creates an editor for a  request based on contents of the given 
   * credential.
   * @param credential basis for the request that will be edited
   * @return editor
   */
  CredentialRequestEditor newEditor(Credential credential);
  
}
