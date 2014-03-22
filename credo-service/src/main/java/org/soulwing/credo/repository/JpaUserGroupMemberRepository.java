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

import java.util.Collection;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.domain.UserGroupMemberEntity;

/**
 * A {@link UserGroupMemberRepository} that is implemented using JPA.
 * 
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaUserGroupMemberRepository
    implements UserGroupMemberRepository {

  @PersistenceContext
  protected EntityManager entityManager;

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(UserGroupMember groupMember) {
    if (!(groupMember instanceof UserGroupMemberEntity)) {
      throw new IllegalArgumentException("unrecognized member type: "
          + groupMember.getClass().getName());
    }

    ((UserGroupMemberEntity) groupMember).setDateCreated(new Date());
    entityManager.persist(groupMember);
  }

  @Override
  public void remove(UserGroupMember groupMember) {
    entityManager.remove(groupMember);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Long id) {
    UserGroupMember groupMember = entityManager.find(
        UserGroupMemberEntity.class, id);
    boolean found = groupMember != null;
    if (found) {
      entityManager.remove(groupMember);
    }
    return found;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserGroupMember findByGroupAndProfileId(String groupName, 
      Long profileId) {
    TypedQuery<UserGroupMember> query = entityManager.createNamedQuery(
        "findGroupMemberWithGroupAndProfileId", UserGroupMember.class);
    query.setParameter("groupName", groupName);
    query.setParameter("profileId", profileId);
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
  public UserGroupMember findByGroupNameAndLoginName(String groupName,
      String loginName) {

    if (UserGroup.SELF_GROUP_NAME.equals(groupName)) {
      groupName = null;
    }

    String queryName =
        groupName != null ? "findGroupMemberWithGroupNameAndLoginName"
            : "findGroupMemberSelf";
    TypedQuery<UserGroupMember> query =
        entityManager.createNamedQuery(queryName, UserGroupMember.class);

    if (groupName != null) {
      query.setParameter("groupName", groupName);
    }
    query.setParameter("loginName", loginName);

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
  public UserGroupMember findByGroupAndLoginName(UserGroup group,
      String loginName) {

    TypedQuery<UserGroupMember> query = entityManager.createNamedQuery(
        "findGroupMemberWithGroupAndLoginName", UserGroupMember.class);

    query.setParameter("group", group);
    query.setParameter("loginName", loginName);

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
  @SuppressWarnings("unchecked")
  public Collection<UserGroupMember> findAllMembers(String groupName) {
    Validate.notEmpty(groupName, "groupName is required");
    Validate.isTrue(!UserGroup.SELF_GROUP_NAME.equals(groupName));

    // We can't use a typed query here because we have more than one 
    // item in the select clause, in order to allow sorting.
    Query query = entityManager.createNamedQuery("findAllGroupMembers");
    query.setParameter("groupName", groupName);

    return (Collection<UserGroupMember>) query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<UserGroupMember> findByLoginName(String loginName) {

    // We can't use a typed query here because we have more than one 
    // item in the select clause, in order to allow sorting.
    Query query = entityManager.createNamedQuery(
        "findGroupsAndMembersByLoginName");
    query.setParameter("loginName", loginName);
    
    return (Collection<UserGroupMember>) query.getResultList();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public Collection<UserGroupMember> findByGroupIdAndLoginName(
      Long groupId, String loginName) {

    // We can't use a typed query here because we have more than one 
    // item in the select clause, in order to allow sorting.
    Query query = entityManager.createNamedQuery(
        "findMembersByGroupIdAndLoginName");
    query.setParameter("groupId", groupId);
    query.setParameter("loginName", loginName);
    
    return (Collection<UserGroupMember>) query.getResultList();
  }

}
