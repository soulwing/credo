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
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.UserProfile;

/**
 * A {@link UserGroupMember} that is a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "user_group_member")
public class UserGroupMemberEntity extends AbstractEntity
    implements UserGroupMember {

  private static final long serialVersionUID = 2207341089346698378L;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private UserProfileEntity user;
  
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private UserGroupEntity group;
  
  @Lob
  @Column(name = "secret_key", nullable = false)
  private String secretKey;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_created", nullable = false)
  private Date dateCreated;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile getUser() {
    return user;
  }

  /**
   * Sets the user.
   * @param user the user to set
   */
  public void setUser(UserProfileEntity user) {
    this.user = user;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup getGroup() {
    return group;
  }

  /**
   * Sets the group.
   * @param group the group to set
   */
  public void setGroup(UserGroupEntity group) {
    this.group = group;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Sets the user's PEM-encoded encrypted copy of the group's secret key.
   * @param secretKey the secret key to set
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
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

}
