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

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
