/*
 * File created on Mar 18, 2014 
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
package org.soulwing.credo.service;

import java.util.Date;


/**
 * An editor for a credential.
 * <p>
 * An instance of this class can be used to display and edit the properties
 * of either a credential or a signing request.
 *
 * @author Carl Harris
 */
public interface CredentialEditor {

  /**
   * Gets the common name for the subject of the credential's certificate.
   * @return subject name
   */
  String getSubjectCommonName();
  
  /**
   * Gets the common name for the issuer of the credential's certificate.
   * @return issuer name or {@code null} if a certificate has not been
   *    issued by a certification authority
   */
  String getIssuerCommonName();
  
  /**
   * Gets the expiration date.
   * @return expiration date
   */
  Date getExpiration();
  
  /**
   * Gets the friendly name for the credential.
   * @return friendly name or {@code null} if no name has been set
   */
  String getName();
  
  /**
   * Sets the friendly name for the credential.
   * @param name the name to set
   */
  void setName(String name);
  
  /**
   * Gets the owner group for the credential.
   * @return owner group (defaults to the "self" group)
   */
  String getOwner();
  
  /**
   * Sets the owner group for the credential.
   * @param owner the owner to set
   */
  void setOwner(String owner);
  
  /**
   * Gets the note for the credential.
   * @return note or {@code null} if none has been set
   */
  String getNote();
  
  /**
   * Sets the note for the credential.
   * @param note the note to set
   */
  void setNote(String note);
  
  /**
   * Gets the tags for the credential.
   * @return array of tags (which may be empty but will never be {@code null})
   */
  String[] getTags();
  
  /**
   * Sets the tags for the credential.
   * @param tags the tags to set
   */
  void setTags(String[] tags);
  
}
