/*
 * File created on Feb 16, 2014 
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
package org.soulwing.credo.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.security.Restricted;
import org.soulwing.credo.security.Restricted.Restriction;

/**
 * A {@link CredentialRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaCredentialRepository implements CredentialRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @Restricted(Restriction.OWNER)
  public void add(Credential credential) {
    if (!(credential instanceof CredentialEntity)) {
      throw new IllegalArgumentException("unsupported credential type: "
          + credential.getClass().getName());
    }
    Set<Tag> tags = new LinkedHashSet<>();
    
    for (Tag tag : credential.getTags()) {
      tags.add(mergeIfNecessary(tag));
    }
    credential.setTags(tags);
    Date now = new Date();
    ((CredentialEntity) credential).setDateCreated(now);
    ((CredentialEntity) credential).setDateModified(now);
    entityManager.persist(credential);
  }

  private Tag mergeIfNecessary(Tag tag) {
    try {
      tag = entityManager.merge(tag);
    }
    catch (IllegalArgumentException ex) {
      assert true;
    }
    return tag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Restricted(Restriction.OWNER)
  public Credential update(Credential credential) {
    return entityManager.merge(credential);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Restricted(Restriction.OWNER)
  public void remove(Credential credential) {
    for (CredentialCertificate certificate : credential.getCertificates()) {
      entityManager.remove(certificate);
    }
    entityManager.remove(credential);
    entityManager.remove(credential.getPrivateKey());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential findById(Long id) {
    TypedQuery<Credential> query = entityManager.createNamedQuery(
        "findCredentialById", Credential.class);
    query.setParameter("id", id);
    try {
      return query.getSingleResult();
    }
    catch (NoResultException ex) {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Credential> findAllByLoginName(String loginName) {
    TypedQuery<Credential> query = entityManager.createNamedQuery(
        "findAllCredentialsByLoginName", Credential.class);
    query.setParameter("loginName", loginName);
    return query.getResultList();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<Credential> findAllByOwners(Collection<UserGroup> owners) {
    TypedQuery<Credential> query = entityManager.createNamedQuery(
        "findAllCredentialsByOwners", Credential.class);
    query.setParameter("owners", ownerIds(owners));
    return query.getResultList();
  }

  private Collection<Long> ownerIds(Collection<UserGroup> owners) {
    List<Long> ownerIds = new ArrayList<>();
    for (UserGroup owner : owners) {
      if (owner.getId() == null) {
        throw new IllegalArgumentException("all owners must be persistent");
      }
      ownerIds.add(owner.getId());
    }
    return ownerIds;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<Credential> findAllByOwnerId(Long ownerId) {
    TypedQuery<Credential> query = entityManager.createNamedQuery(
        "findAllCredentialsByOwnerId", Credential.class);
    query.setParameter("ownerId", ownerId);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential findByRequestId(Long requestId) {
    TypedQuery<Credential> query = entityManager.createNamedQuery(
        "findCredentialByRequestId", Credential.class);
    query.setParameter("requestId", requestId);
    try {
      return query.getSingleResult();      
    }
    catch (NoResultException ex) {
      return null;
    }
  }

}
