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
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.UserGroupEntity;

/**
 * A {@link UserGroupRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaUserGroupRepository implements UserGroupRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup newGroup(String name) {
    return new UserGroupEntity(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(UserGroup group) {
    if (!(group instanceof UserGroupEntity)) {
      throw new IllegalArgumentException("unrecognized group type: "
          + group.getClass().getName());
    }
    Date now = new Date();
    ((UserGroupEntity) group).setDateCreated(now);
    ((UserGroupEntity) group).setDateModified(now);
    entityManager.persist(group);    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup findByGroupName(String groupName) {
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        "findUserGroupsByName", UserGroup.class);
    query.setParameter("groupName", groupName);
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
  public Set<? extends UserGroup> findByLoginName(String loginName) {
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        "findUserGroupsByLoginName", UserGroup.class);
    query.setParameter("loginName", loginName);
    Set<UserGroup> groups = new LinkedHashSet<>();
    groups.addAll(query.getResultList());
    return groups;
  }

}
