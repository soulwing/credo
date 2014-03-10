/*
 * File created on Mar 3, 2014 
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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.UserGroup;

/**
 * A {@link UserGroup} that is a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "user_group")
public class UserGroupEntity extends AbstractEntity implements UserGroup {

  private static final long serialVersionUID = 7936165567654587951L;

  @Column(name = "group_name", unique = true)
  private String name;
  
  @Column(name = "description")
  private String description;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_created")
  private Date dateCreated;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_modified")
  private Date dateModified;
  
  /**
   * Constructs a new instance.
   */
  public UserGroupEntity() {    
  }
  
  /**
   * Constructs a new instance.
   * @param name the name of the group
   */
  public UserGroupEntity(String name) {
    if (UserGroup.SELF_GROUP_NAME.equals(name)) {
      name = null;
    }
    this.name = name;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    if (name == null) return UserGroup.SELF_GROUP_NAME;
    return name;
  }

  /**
   * {@inheritDoc} 
   */
  @Override
  public void setName(String name) {
    if (UserGroup.SELF_GROUP_NAME.equals(name)) {
      name = null;
    }
    this.name = name;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the {@code dateCreated} property.
   * @return
   */
  public Date getDateCreated() {
    return dateCreated;
  }

  /**
   * Sets the {@code dateCreated} property.
   * @param dateCreated
   */
  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  /**
   * Gets the {@code dateModified} property.
   * @return
   */
  public Date getDateModified() {
    return dateModified;
  }

  /**
   * Sets the {@code dateModified} property.
   * @param dateModified
   */
  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  @Override
  public int hashCode() {
    if (getId() == 0) return 0;
    return getId().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof UserGroupEntity)) return false;
    UserGroupEntity that = (UserGroupEntity) obj;
    if (this.getId() == null || that.getId() == null) return false;
    return this.getId().equals(that.getId());
  }

}
