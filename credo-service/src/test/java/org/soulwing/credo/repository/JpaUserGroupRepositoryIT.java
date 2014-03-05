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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

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
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.domain.UserGroupEntity;
import org.soulwing.credo.domain.UserGroupMemberEntity;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Integration tests for {@link JpaUserGroupRepository}.
 *
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class JpaUserGroupRepositoryIT {

  @Deployment
  public static Archive<?> createDeployment() {
      return ShrinkWrap.create(WebArchive.class)
          .addPackage(UserGroup.class.getPackage())
          .addPackage(UserGroupEntity.class.getPackage())
          .addClasses(UserGroupRepository.class, JpaUserGroupRepository.class)
          .addAsResource("persistence-test.xml", "META-INF/persistence.xml")
          .addAsResource("META-INF/orm.xml", "META-INF/orm.xml")
          .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }
  
  @Inject
  private UserGroupRepository repository;
  
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
    UserGroupEntity expected = new UserGroupEntity("someGroup");
    repository.add(expected);
    entityManager.flush();
    entityManager.clear();
    UserGroupEntity actual = entityManager.find(UserGroupEntity.class,
        expected.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(expected.getName())));
    assertThat(actual.getDateCreated(), is(not(nullValue())));
    assertThat(actual.getDateModified(), is(equalTo(actual.getDateCreated())));
  }
  
  @Test
  public void testFindByGroupName() throws Exception {
    final String groupName = "someGroup";
    final String loginName = null;
    final UserGroupEntity group = EntityUtil.newGroup(groupName);
    entityManager.persist(group);
    entityManager.flush();
    entityManager.clear();
    
    UserGroup actual = repository.findByGroupName(groupName, loginName);
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(group.getName())));
  }

  @Test
  public void testFindGroupSelf() throws Exception {
    final String groupName = UserGroup.SELF_GROUP_NAME;
    final String loginName = "someUser";
    final UserGroupEntity group = EntityUtil.newGroup(groupName);
    final UserProfileEntity user = EntityUtil.newUser(loginName);
    final UserGroupMember groupMember = EntityUtil.newGroupMember(user, group);
    entityManager.persist(group);
    entityManager.persist(user);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroup actual = repository.findByGroupName(groupName, loginName);
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(UserGroup.SELF_GROUP_NAME)));
  }
  
  @Test
  public void testFindByGroupNameWhenNotFound() throws Exception {
    UserGroup actual = repository.findByGroupName("does not exist", 
        null);
    assertThat(actual, is(nullValue()));
  }

  @Test
  public void testFindByLoginName() throws Exception {
    final String loginName = "someUser";
    UserProfileEntity user = EntityUtil.newUser("someUser");
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    Set<? extends UserGroup> groups = 
        repository.findByLoginName(loginName);
    assertThat(groups, contains(hasProperty("name", equalTo("someGroup"))));
  }

}
