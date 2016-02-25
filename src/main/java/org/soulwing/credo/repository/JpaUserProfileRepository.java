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
package org.soulwing.credo.repository;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.soulwing.credo.UserProfile;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * A {@link UserProfileRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaUserProfileRepository implements UserProfileRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void add(UserProfile profile) {
    if (!(profile instanceof UserProfileEntity)) {
      throw new IllegalArgumentException("unsupported profile type: "
          + profile.getClass().getName());
    }
    Date now = new Date();
    ((UserProfileEntity) profile).setDateCreated(now);
    ((UserProfileEntity) profile).setDateModified(now);
    entityManager.persist(profile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserProfile> findAll() {
    TypedQuery<UserProfile> query = entityManager.createNamedQuery(
        "findAllUserProfiles", UserProfile.class);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile findById(Long id) {
    return entityManager.find(UserProfileEntity.class, id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserProfile findByLoginName(String loginName) {
    TypedQuery<UserProfile> query = 
        entityManager.createNamedQuery("findUserProfileByLoginName", 
            UserProfile.class);
    query.setParameter("loginName", loginName);
    try {
      return query.getSingleResult();
    }
    catch (NoResultException ex) {
      return null;
    }
  }

}
