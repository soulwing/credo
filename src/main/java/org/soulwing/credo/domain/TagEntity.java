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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.soulwing.credo.Tag;

/**
 * A {@link Tag} implemented as a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name ="tag")
public class TagEntity extends AbstractEntity implements Tag {

  private static final long serialVersionUID = -5776251887144095935L;

  @Column(unique = true, nullable = false, length = 50)
  private String text;
  
  /**
   * Constructs a new instance.
   */
  public TagEntity() {    
  }

  /**
   * Constructs a new instance.
   * @param text
   */
  public TagEntity(String text) {
    this.text = text;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getText() {
    return text;
  }

  /**
   * Sets the {@code text} property.
   * @param text
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    if (text == null) return 0;
    return text.hashCode();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof TagEntity)) return false;
    if (text == null) return false;
    return text.equals(((TagEntity) obj).text);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return text;
  }

}
