/*
 * File created on Mar 20, 2014 
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
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificationRequest;
import org.soulwing.credo.CredentialKey;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;

/**
 * A {@link CredentialRequest} implemented as a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "credential_request")
public class CredentialRequestEntity extends AbstractEntity 
    implements CredentialRequest {

  private static final long serialVersionUID = -2905017534649453847L;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private UserGroupEntity owner;

  @Column(nullable = false)
  private String subject;
  
  @Lob
  private String note;
  
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "credential_request_tag", inverseJoinColumns = { 
      @JoinColumn(name = "tag_id")
  })
  private Set<TagEntity> tags = new LinkedHashSet<TagEntity>();

  @OneToOne(optional = false, fetch = FetchType.LAZY, 
      cascade = { CascadeType.PERSIST })
  @JoinColumn(name = "private_key_id")
  private CredentialKeyEntity privateKey = new CredentialKeyEntity();

  @OneToOne(optional = false, fetch = FetchType.LAZY,
      cascade = { CascadeType.PERSIST })
  @JoinColumn(name = "certification_request_id")
  private CredentialCertificationRequestEntity certificationRequest =
      new CredentialCertificationRequestEntity();

  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  private CredentialEntity credential;
  
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
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup getOwner() {
    return owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(UserGroup owner) {
    if (!(owner instanceof UserGroupEntity)) {
      throw new IllegalArgumentException("unsupported group type: "
          + owner.getClass().getName());
    }
    this.owner = (UserGroupEntity) owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * {@inheritDoc}
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getNote() {
    return note;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<? extends Tag> getTags() {
    return tags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTags(Set<? extends Tag> tags) {
    this.tags = new LinkedHashSet<TagEntity>();
    for (Tag tag : tags) {
      if (!(tag instanceof TagEntity)) {
        throw new IllegalArgumentException("unexpected tag type: " 
            + tag.getClass().getName());
      }
      addTag((TagEntity) tag);
    }
  }

  /**
   * Adds a tag to the receiver.
   * @param tag the tag to add
   */
  public void addTag(TagEntity tag) {
    this.tags.add(tag);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialKey getPrivateKey() {
    return privateKey;
  }

  /**
   * Sets the request's private key.
   * @param privateKey private key.
   */
  public void setPrivateKey(CredentialKeyEntity privateKey) {    
    this.privateKey = privateKey;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialCertificationRequest getCertificationRequest() {
    return certificationRequest;
  }

  /**
   * Sets the request's certification request content.
   * @param certificationRequest the certification request to set
   */
  public void setCertificationRequest(
      CredentialCertificationRequestEntity certificationRequest) {
    this.certificationRequest = certificationRequest;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Credential getCredential() {
    return credential;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCredential(Credential credential) {
    if (!(credential instanceof CredentialEntity)) {
      throw new IllegalArgumentException("unsupported credential type: "
          + credential.getClass().getName());
    }
    this.credential = (CredentialEntity) credential;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getDateCreated() {
    return dateCreated;
  }

  /**
   * Sets the date/time at which this request was created.
   * @param dateCreated the date to set
   */
  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  /**
   * Sets the date/time at which this request was last modified.
   * @param dateModified the date to set
   */
  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

}
