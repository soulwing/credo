/*
 * File created on Mar 19, 2014 
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
package org.soulwing.credo;

import java.util.Date;
import java.util.Set;

/**
 * An abstract representation of a request for a new credential.
 * <p>
 * A request shares some common properties with a credential, such as a 
 * friendly name, note, tags, and an owner group.  Additionally, it has
 * a private key and a PKCS#10 certification request document. 
 *
 * @author Carl Harris
 */
public interface CredentialRequest extends Owned {

  /**
   * Gets the persistent unique identifier for this request.
   * @return unique identifier or {@code null} if this request is transient
   */
  Long getId();
  
  /**
   * Gets the friendly name associated with this request.
   * @return friendly name
   */
  String getName();
  
  /**
   * Sets the friendly name associated with this request.
   * @param name the friendly name to set
   */
  void setName(String name);

  /**
   * Gets the group that owns this request.
   * @return owner group or {@code null} if no owner has been assigned
   */
  UserGroup getOwner();
  
  /**
   * Sets the group that owns this request.
   * @param owner the owner group to set
   */
  void setOwner(UserGroup owner);

  /**
   * Gets the subject name in the certification request.
   * @return subject name
   */
  String getSubject();

  /**
   * Gets the note associated with this request.
   * @return description
   */
  String getNote();
  
  /**
   * Sets the note associated with this request.
   * @param note the note to set
   */
  void setNote(String note);
  
  /**
   * Gets the collection of tags assigned to this request.
   * @return tag set
   */
  Set<? extends Tag> getTags();
  
  /**
   * Sets (replaces) the collection of tags assigned to this request.
   * @param tags the tags to set
   */
  void setTags(Set<? extends Tag> tags);

  /**
   * Gets the private key for this request.
   * @return private key
   */
  CredentialKey getPrivateKey();
  
  /**
   * Gets the certification document for this request.
   * @return certification request
   */
  CredentialCertificationRequest getCertificationRequest();
  
  /**
   * Gets the date/time at which this request was created.
   * @return creation date
   */
  Date getDateCreated();
  
  /**
   * Gets the credential that is the basis for this request, if any.
   * <p>
   * When a request is created to renew an existing credential, this property
   * refers to the credential that is to be renewed.
   * 
   * @return credential or {@code null} if this request was not created to
   *    renew an existing credential
   */
  Credential getCredential();
  
  /**
   * Sets an existing credential that is the basis for this request.
   * <p>
   * When a request is created to renew an existing credential, this property
   * refers to the credential that is to be renewed.
   * 
   * @param credential the credential to set
   */
  void setCredential(Credential credential);

}
