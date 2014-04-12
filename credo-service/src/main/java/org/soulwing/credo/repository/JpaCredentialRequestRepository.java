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

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.CredentialRequestEntity;
import org.soulwing.credo.security.Restricted;
import org.soulwing.credo.security.Restricted.Restriction;

/**
 * A {@link CredentialRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaCredentialRequestRepository 
    implements CredentialRequestRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @Restricted(Restriction.OWNER)
  public void add(CredentialRequest request) {
    if (!(request instanceof CredentialRequestEntity)) {
      throw new IllegalArgumentException("unsupported request type: "
          + request.getClass().getName());
    }
    Set<Tag> tags = new LinkedHashSet<>();
    
    for (Tag tag : request.getTags()) {
      tags.add(mergeIfNecessary(tag));
    }
    request.setTags(tags);
    Date now = new Date();
    ((CredentialRequestEntity) request).setDateCreated(now);
    ((CredentialRequestEntity) request).setDateModified(now);
    entityManager.persist(request);
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
  public CredentialRequest update(CredentialRequest request) {
    return entityManager.merge(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Restricted(Restriction.OWNER)
  public void remove(CredentialRequest request, boolean removePrivateKey) {
    if (request != null) {
      entityManager.remove(request);
      entityManager.remove(request.getCertificationRequest());
      if (removePrivateKey) {
        entityManager.remove(request.getPrivateKey());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequest findById(Long id) {
    TypedQuery<CredentialRequest> query = entityManager.createNamedQuery(
        "findRequestById", CredentialRequest.class);
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
  public List<CredentialRequest> findAllByLoginName(String loginName) {
    TypedQuery<CredentialRequest> query = entityManager.createNamedQuery(
        "findAllRequestsByLoginName", CredentialRequest.class);
    query.setParameter("loginName", loginName);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CredentialRequest> findAllByOwners(Collection<UserGroup> owners) {
    TypedQuery<CredentialRequest> query = entityManager.createNamedQuery(
        "findAllRequestsByOwners", CredentialRequest.class);
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
  
}
