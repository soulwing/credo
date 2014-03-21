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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;

/**
 * A {@link Credential} that is a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "credential")
public class CredentialEntity extends AbstractEntity implements Credential {

  private static final long serialVersionUID = 641502440794773525L;
  
  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private UserGroupEntity owner;
  
  @Lob
  private String note;
  
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "credential_tag", inverseJoinColumns = { 
      @JoinColumn(name = "tag_id")
  })
  private Set<TagEntity> tags = new LinkedHashSet<TagEntity>();
  
  @Column(nullable = false)
  private String issuer;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date expiration;
  
  @OneToOne(optional = false, fetch = FetchType.LAZY, 
      cascade = { CascadeType.PERSIST })
  @JoinColumn(name = "private_key_id")
  private CredentialKeyEntity privateKey;
  
  @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
  @JoinTable(name = "credential_certificates")
  @OrderColumn(name = "list_offset")
  private List<CredentialCertificateEntity> certificates = 
      new ArrayList<>();
  
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
  public Set<TagEntity> getTags() {
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

  public void addTag(TagEntity tag) {
    this.tags.add(tag);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuer() {
    return issuer;
  }

  /**
   * Sets the name of the issuer of the credential's certificate.
   * @param issuer the issuer name to set
   */
  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getExpiration() {
    return expiration;
  }
  
  /**
   * Sets the expiration date of the credential's certificate
   * @param expiration the expiration date to set
   */
  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialKeyEntity getPrivateKey() {
    return privateKey;
  }

  /**
   * Sets the receiver's private key.
   * @param privateKey the private key to set
   */
  public void setPrivateKey(CredentialKeyEntity privateKey) {
    this.privateKey = privateKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CredentialCertificateEntity> getCertificates() {
    return certificates;
  }

  /**
   * Sets the receiver's chain of certificates.
   * @param certificates the certificates to set
   */
  public void setCertificates(
      List<CredentialCertificateEntity> certificates) {
    this.certificates = certificates;
  }
  
  /**
   * Adds a certificate to the receiver's chain of certificates.
   * @param certificate the certificate to add
   */
  public void addCertificate(
      CredentialCertificateEntity certificate) {
    if (certificate == null) {
      throw new NullPointerException("cannot add null reference to list");
    }
    this.certificates.add(certificate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getDateCreated() {
    return dateCreated;
  }

  /**
   * Sets the date at which this credential was created.
   * @param dateCreated the creation date to set
   */
  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Date getDateModified() {
    return dateModified;        
  }

  /**
   * Sets the date at which this credential was last modified.
   * @param dateModified the last modification date to set
   */
  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

}
