/*
 * File created on Feb 21, 2014 
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
package org.soulwing.credo.facelets;

import java.util.Date;
import java.util.Set;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;

/**
 * A bean that simplifies the structure of a {@link Credential} for use
 * in a UI view.
 *
 * @author Carl Harris
 */
public class CredentialBean {

  private final Credential credential;

  /**
   * Constructs a new instance.
   * @param credential
   */
  public CredentialBean(Credential credential) {
    this.credential = credential;
  }

  /**
   * Gets the delegate credential.
   * <p>
   * This method is exposed to support unit testing.
   * @return delegate
   */
  Credential getDelegate() {
    return credential;
  }
  
  /**
   * Gets the friendly name assigned to the credential.
   * @return friendly name
   */
  public String getName() {
    return credential.getName();
  }
  
  /**
   * Gets the name of the credential's issuer.
   * @return issuer name
   */
  public String getIssuer() {
    return null;
  }
  
  /**
   * Gets the date at which the credential's certificate expires.
   * @return expiration date
   */
  public Date getExpiration() {
    return null;
  }
  
  /**
   * Gets the tags assigned to this credential.
   * @return tags
   */
  public String getTags() {
    StringBuilder sb = new StringBuilder();
    Set<? extends Tag> tags = credential.getTags();
    int i = 0;
    int size = tags.size();
    for (Tag tag : tags) {
      sb.append(tag.getText());
      if (++i < size) {
        sb.append(", ");
      }     
    }
    return sb.toString();
  }
  
}
