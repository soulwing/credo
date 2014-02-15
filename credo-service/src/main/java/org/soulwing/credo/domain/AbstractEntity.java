/*
 * File created on Feb 14, 2014 
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

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * An abstract base for entity classes.
 * 
 * @author Carl Harris
 */
@MappedSuperclass
public class AbstractEntity implements Serializable {

  private static final long serialVersionUID = 2429649536505196748L;

  private Long id;
  private Long version;

  /**
   * Gets the {@code id} property.
   * @return
   */
  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  /**
   * Sets the {@code id} property.
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the {@code version} property.
   * @return
   */
  @Version
  public Long getVersion() {
    return version;
  }

  /**
   * Sets the {@code version} property.
   * @param version
   */
  public void setVersion(Long version) {
    this.version = version;
  }

}
