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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;

/**
 * A {@link Credential} that is a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "credential")
public class CredentialEntity extends AbstractEntity implements Credential {

  private static final long serialVersionUID = 641502440794773525L;
  
  private String name;
  private String note;
  private Set<TagEntity> tags = new LinkedHashSet<TagEntity>();
  
  private CredentialKeyEntity privateKey;
  private List<CredentialCertificateEntity> certificates = 
      new ArrayList<>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  @Column(unique = true, nullable = false, length = 100)
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
  @Lob
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
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "credential_tag", inverseJoinColumns = { 
      @JoinColumn(name = "tag_id")
  })
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
  @OneToOne(optional = false, fetch = FetchType.LAZY, 
      mappedBy = "credential", orphanRemoval = true, 
      cascade = { CascadeType.PERSIST })
  public CredentialKeyEntity getPrivateKey() {
    return privateKey;
  }

  /**
   * Sets the receiver's private key.
   * @param privateKey the private key to set
   */
  public void setPrivateKey(CredentialKeyEntity privateKey) {
    this.privateKey = privateKey;
    if (privateKey != null) {
      privateKey.setCredential(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "credential",
      cascade = { CascadeType.PERSIST })
  @OrderColumn(name = "list_offset")
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
    certificate.setCredential(this);    
  }

}
