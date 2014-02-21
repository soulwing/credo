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

  @Id
  @GeneratedValue
  private Long id;
  
  @Version
  private Long version;

  /**
   * Gets the {@code id} property.
   * @return
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the {@code version} property.
   * @return
   */
  public Long getVersion() {
    return version;
  }

}
