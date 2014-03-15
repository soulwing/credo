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
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.Validate;
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
          .addClass(EntityUtil.class)
          .addClass(Validate.class)
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
    UserProfileEntity user = EntityUtil.newUser("someUser");
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    UserGroupMemberEntity expected = EntityUtil.newGroupMember(user, group);
    
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
  
  @Test
  public void testFindAllGroupMembers() throws Exception {
    final String loginName1 = "someUser1";
    final String loginName2 = "someUser2";
    final String groupName = "someGroup";
    UserProfileEntity user1 = EntityUtil.newUser(loginName1);
    UserProfileEntity user2 = EntityUtil.newUser(loginName2);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember1 = EntityUtil.newGroupMember(user1, group);
    UserGroupMemberEntity groupMember2 = EntityUtil.newGroupMember(user2, group);
    
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.persist(group);
    entityManager.persist(groupMember1);
    entityManager.persist(groupMember2);
    entityManager.flush();
    entityManager.clear();
    
    Collection<UserGroupMember> actual = repository.findAllMembers(groupName);
    assertThat(actual, hasProperty("empty", equalTo(false)));
    
    Iterator<UserGroupMember> i = actual.iterator();

    assertThat(i.hasNext(), is(true));
    UserGroupMember member = i.next();
    assertThat(member, 
        hasProperty("group", hasProperty("name", equalTo(groupName))));
    assertThat(member, 
        hasProperty("user", hasProperty("loginName", equalTo(loginName1))));

    assertThat(i.hasNext(), is(true));
    member = i.next();
    assertThat(member, 
        hasProperty("group", hasProperty("name", equalTo(groupName))));
    assertThat(member, 
        hasProperty("user", hasProperty("loginName", equalTo(loginName2))));

    assertThat(i.hasNext(), is(false));
  }
  
  @Test
  public void testFindByGroupAndProfileId() throws Exception {
    final String loginName = "someUser";
    final String groupName = "someGroup";
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMember actual = repository.findByGroupAndProfileId(
        groupName, user.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual, 
        hasProperty("group", hasProperty("name", equalTo(groupName))));
    assertThat(actual, 
        hasProperty("user", hasProperty("loginName", equalTo(loginName))));
  }

  @Test
  public void testFindByGroupAndLoginName() throws Exception {
    final String loginName = "someUser";
    final String groupName = "someGroup";
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMember actual = repository.findByGroupAndLoginName(
        groupName, loginName);
    assertThat(actual, is(not(nullValue())));
    assertThat(actual, 
        hasProperty("group", hasProperty("name", equalTo(groupName))));
    assertThat(actual, 
        hasProperty("user", hasProperty("loginName", equalTo(loginName))));
  }

  @Test
  public void testFindByGroupAndLoginNameWhenGroupNotFound() throws Exception {
    final String loginName = "someUser";
    final String groupName = "someGroup";
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMember actual = repository.findByGroupAndLoginName(
        "someOtherGroup", loginName);
    assertThat(actual, is(nullValue()));
  }

  @Test
  public void testFindByGroupAndLoginNameWhenSelfGroup() throws Exception {
    final String loginName = "someUser";
    final String groupName = UserGroup.SELF_GROUP_NAME;
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMember actual = repository.findByGroupAndLoginName(groupName, 
        loginName);
    assertThat(actual, is(not(nullValue())));
  }

  @Test
  public void testFindByGroupAndLoginNameWhenGroupIsNull() throws Exception {
    final String loginName = "someUser";
    final String groupName = null;
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    UserGroupMember actual = repository.findByGroupAndLoginName(groupName, 
        loginName);
    assertThat(actual, is(not(nullValue())));
  }

  @Test
  public void testFindByLoginName() throws Exception {
    UserProfileEntity user1 = EntityUtil.newUser("someUser1");
    UserProfileEntity user2 = EntityUtil.newUser("someUser2");
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    
    UserGroupMemberEntity groupMember1 = EntityUtil.newGroupMember(user1, group);
    UserGroupMemberEntity groupMember2 = EntityUtil.newGroupMember(user2, group);
    
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.persist(group);
    entityManager.persist(groupMember1);
    entityManager.persist(groupMember2);
    entityManager.flush();
    entityManager.clear();
    
    Iterator<UserGroupMember> members = 
        repository.findByLoginName(user1.getLoginName()).iterator();

    assertThat(members.hasNext(), is(true));
    UserGroupMember member1 = members.next();
    assertThat(member1.getUser(), 
        hasProperty("loginName", equalTo(user1.getLoginName())));

    assertThat(members.hasNext(), is(true));
    UserGroupMember member2 = members.next();
    assertThat(member2.getUser(), 
        hasProperty("loginName", equalTo(user2.getLoginName())));
    
    assertThat(members.hasNext(), is(false));
  }

}
