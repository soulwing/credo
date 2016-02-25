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

import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.service.credential.CredentialEditor;



/**
 * An editor for the properties of a credential request.
 *
 * @author Carl Harris
 */
public interface CredentialRequestEditor extends CredentialEditor {

  /**
   * Gets the unique identifier of the the credential this is the basis
   * for the request.
   * <p>
   * @return credential ID or {@code null} if this request is not associated 
   *    with an existing credential
   */
  Long getCredentialId();
  
  /**
   * Gets the subject name for the signing request.
   * @return subject name
   */
  X500Principal getSubjectName();
  
  /**
   * Sets the subject name for the signing request.
   * @param subjectName the subject name to set
   */
  void setSubjectName(X500Principal subjectName);
  
}
