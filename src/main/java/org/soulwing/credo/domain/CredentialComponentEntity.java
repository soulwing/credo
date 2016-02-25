/*
 * File created on Feb 18, 2014 
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
package org.soulwing.credo.domain;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

/**
 * A component entity for a {@link CredentialEntity}.
 * <p>
 *
 * @author Carl Harris
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class CredentialComponentEntity extends AbstractEntity {

  private static final long serialVersionUID = -7847231056940730063L;

  @Column(name = "content", nullable = false, length = 65535)
  private String encoded;

  /**
   * Gets the {@code encoded} property.
   * @return
   */
  public String getEncoded() {
    return encoded;
  }

  /**
   * Sets the {@code encoded} property.
   * @param encoded
   */
  public void setEncoded(String encoded) {
    this.encoded = encoded;
  }
  
  /**
   * {@inheritDoc}
   */
  public String getContent() {
    return getEncoded();
  }

}
