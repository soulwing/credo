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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.UserProfile;

/**
 * A {@link UserProfile} that is a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "user_profile")
public class UserProfileEntity extends AbstractEntity implements UserProfile {

  private static final long serialVersionUID = -4625919386950644573L;

  @Column(name = "login_name", nullable = false, unique = true)
  private String loginName;
  
  @Column(name = "full_name", nullable = false)
  private String fullName;
  
  @Column(name = "encrypted_password", nullable = false)
  private String password;
  
  @Column(name = "public_key", nullable = false, length = 65535)
  private String publicKey;
  
  @Column(name = "private_key", nullable = false, length = 65535)
  private String privateKey;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_created", nullable = false)
  private Date dateCreated;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_modified", nullable = false)
  private Date dateModified;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getLoginName() {
    return loginName;
  }

  /**
   * Sets the login name.
   * @param loginName the login name to set
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets the full name.
   * @param fullName the full name to set
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Sets the encrypted password.
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getPublicKey() {
    return publicKey;
  }

  /**
   * Sets the PEM-encoded public key.
   * @param publicKey the public key to set
   */
  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrivateKey() {
    return privateKey;
  }

  /**
   * Sets the PEM-encoded encrypted private key.
   * @param privateKey the private key to set
   */
  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    if (getId() == 0) return 0;
    return getId().hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof UserProfileEntity)) return false;
    UserProfileEntity that = (UserProfileEntity) obj;
    if (this.getId() == null || that.getId() == null) return false;
    return this.getId().equals(that.getId());
  }

}
