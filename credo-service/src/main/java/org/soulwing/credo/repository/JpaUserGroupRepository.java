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
  public UserGroup update(UserGroup group) {
    if (!(group instanceof UserGroupEntity)) {
      throw new IllegalArgumentException("unrecognized group type: "
          + group.getClass().getName());
    }
    Date now = new Date();
    ((UserGroupEntity) group).setDateModified(now);
    return entityManager.merge(group);
  }

  @Override
  public boolean remove(Long id) {
    UserGroupEntity group = entityManager.find(UserGroupEntity.class, id);
    boolean found = group != null;
    if (found) {
      entityManager.remove(group);
    }
    return found;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup findById(Long id) {
    return entityManager.find(UserGroupEntity.class, id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroup findByGroupName(String groupName, String loginName) {
    
    if (UserGroup.SELF_GROUP_NAME.equals(groupName)) {
      groupName = null;
    }
    
    String queryName = groupName != null ? "findGroupByName" : "findGroupSelf";
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        queryName, UserGroup.class);
    
    if (groupName != null) {
      query.setParameter("groupName", groupName);
    }
    else {
      query.setParameter("loginName", loginName);
    }
    
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
  public List<UserGroup> findByLoginName(String loginName) {
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        "findGroupsByLoginName", UserGroup.class);
    query.setParameter("loginName", loginName);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserGroup> findByOwner(UserGroup owner) {
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        "findGroupsByOwner", UserGroup.class);
    query.setParameter("owner", owner);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserGroup> findDescendants(UserGroup group) {
    TypedQuery<UserGroup> query = entityManager.createNamedQuery(
        "findGroupDescendants", UserGroup.class);
    query.setParameter("pattern", String.format("%%%s%d%s%%",
        UserGroup.PATH_DELIMITER, group.getId(), UserGroup.PATH_DELIMITER));
    return query.getResultList();
  }

}
