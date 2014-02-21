/*
 * File created on Feb 13, 2014 
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
import java.util.List;
import java.util.Set;


/**
 * An abstract representation of an X.509 credential.
 *
 * @author Carl Harris
 */
public interface Credential {

  /**
   * Gets the simple name assigned to this credential.
   * @return name
   */
  String getName();
  
  /**
   * Sets the simple name assigned to this credential.
   * @param name the name to set
   */
  void setName(String name);

  /**
   * Gets the note associated with this credential.
   * @return description
   */
  String getNote();
  
  /**
   * Sets the note associated with assigned to this credential.
   * @param note the note to set
   */
  void setNote(String note);
  
  /**
   * Gets the collection of tags assigned to this credential.
   * @return tag set
   */
  Set<? extends Tag> getTags();
  
  /**
   * Sets (replaces) the collection of tags assigned to this credential.
   * @param tags the tags to set
   */
  void setTags(Set<? extends Tag> tags);
  
  /**
   * Gets the name of this credential's certificate issuer.
   * @return issuer name
   */
  String getIssuer();
  
  /**
   * Gets the expiration date of this credential's certificate.
   * @return 
   */
  Date getExpiration();
  
  /**
   * Gets the private key for this credential.
   * @return private key
   */
  CredentialKey getPrivateKey();
  
  /**
   * Gets the chain of certificates for this credential.
   * @return certificate list
   */
  List<? extends CredentialCertificate> getCertificates();
  
  /**
   * Gets the date at which this credential was created.
   * @return creation date
   */
  Date getDateCreated();
  
  /**
   * Gets the date at which this credential was last modified;
   * @return last modification date
   */
  Date getDateModified();
  
}
