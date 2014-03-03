/*
 * File created on Feb 17, 2014 
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.domain.UserGroupEntity;
import org.soulwing.credo.domain.UserGroupMemberEntity;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Integration tests for {@link JpaUserGroupMemberRepository}.
 *
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class JpaUserGroupMemberRepositoryIT {

  @Deployment
  public static Archive<?> createDeployment() {
      return ShrinkWrap.create(WebArchive.class)
          .addPackage(UserGroupMember.class.getPackage())
          .addPackage(UserGroupMemberEntity.class.getPackage())
          .addClasses(UserGroupMemberRepository.class, 
              JpaUserGroupMemberRepository.class)
          .addAsResource("persistence-test.xml", "META-INF/persistence.xml")
          .addAsResource("META-INF/orm.xml", "META-INF/orm.xml")
          .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }
  
  @Inject
  private UserGroupMemberRepository repository;
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Inject
  private UserTransaction tx;
  
  @Before
  public void setUp() throws Exception {
    tx.begin();
    entityManager.joinTransaction();
  }
  
  @After
  public void tearDown() throws Exception {
    tx.rollback();
  }
  
  @Test
  public void testAdd() throws Exception {
    UserProfileEntity user = newUser("someUser");
    UserGroupEntity group = newGroup("someGroup");
    UserGroupMemberEntity expected = newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    repository.add(expected);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMemberEntity actual = 
        entityManager.find(UserGroupMemberEntity.class, expected.getId());
    
    assertThat(((UserProfileEntity) actual.getUser()).getId(), 
        is(equalTo(user.getId())));
    assertThat(((UserGroupEntity) actual.getGroup()).getId(), 
        is(equalTo(group.getId())));
    assertThat(actual.getSecretKey(), is(equalTo(expected.getSecretKey())));
    assertThat(actual.getDateCreated(), is(not(nullValue())));
  }
  
  private UserProfileEntity newUser(String loginName) {
    UserProfileEntity user = new UserProfileEntity();
    Date now = new Date();
    user.setLoginName(loginName);
    user.setFullName("Some User");
    user.setPassword("some password");
    user.setPublicKey("some public key");
    user.setPrivateKey("some public key");
    user.setDateCreated(now);
    user.setDateModified(now);
    user.getDateCreated();
    return user;
  }
  
  private UserGroupEntity newGroup(String name) {
    UserGroupEntity group = new UserGroupEntity();
    Date now = new Date();
    group.setName("someGroup");
    group.setDateCreated(now);
    group.setDateModified(now);
    return group;
  }
  
  private UserGroupMemberEntity newGroupMember(UserProfileEntity user,
      UserGroupEntity group) {
    UserGroupMemberEntity groupMember = new UserGroupMemberEntity();
    groupMember.setUser(user);
    groupMember.setGroup(group);
    groupMember.setSecretKey("some secret key");
    return groupMember;
  }

}
