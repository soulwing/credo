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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

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
          .addClass(EntityUtil.class)
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
  public void testRemove() throws Exception {
    UserGroupEntity group = new UserGroupEntity("someGroup");
    repository.add(group);
    entityManager.flush();
    entityManager.clear();
    
    assertThat(repository.remove(group.getId()), is(true));
    entityManager.flush();
    entityManager.clear();
    
    assertThat(entityManager.find(UserGroupEntity.class, group.getId()),
        is(nullValue()));
  }


  @Test
  public void testUpdate() throws Exception {
    UserGroupEntity expected = new UserGroupEntity("someGroup");
    repository.add(expected);
    entityManager.flush();
    entityManager.clear();
    Thread.sleep(250);
    expected.setDescription("updated description");
    repository.update(expected);
    entityManager.flush();
    entityManager.clear();
    UserGroupEntity actual = entityManager.find(UserGroupEntity.class,
        expected.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(expected.getName())));
    assertThat(actual.getDescription(), containsString("updated"));
    assertThat(actual.getDateCreated(), is(not(nullValue())));
    assertThat(actual.getDateModified(), is(not(equalTo(actual.getDateCreated()))));
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
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    List<UserGroup> groups = repository.findByLoginName(loginName);
    assertThat(groups, contains(hasProperty("name", equalTo("someGroup"))));
  }

  @Test
  public void testFindByOwnerWithChild() throws Exception {
    UserProfileEntity user = EntityUtil.newUser("someUser");
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    UserGroupEntity owner = EntityUtil.newGroup("someOwner");
    
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    
    entityManager.persist(user);
    entityManager.persist(owner);

    group.setOwner(owner);
    entityManager.persist(group);
    entityManager.persist(groupMember);
    entityManager.flush();
    entityManager.clear();
    
    List<UserGroup> groups = repository.findByOwner(owner);
    assertThat(groups, contains(hasProperty("name", equalTo("someGroup"))));

    
    assertThat(groups, contains(hasProperty("ancestryPath", 
        equalTo(makePath(owner.getId())))));
  }

  @Test
  public void testFindByOwnerWithGrandchild() throws Exception {
    UserGroupEntity child = EntityUtil.newGroup("child");
    UserGroupEntity grandchild = EntityUtil.newGroup("grandchild");
    UserGroupEntity parent = EntityUtil.newGroup("parent");
    
    
    entityManager.persist(parent);

    child.setOwner(parent);
    entityManager.persist(child);
    
    grandchild.setOwner(child);
    entityManager.persist(grandchild);

    entityManager.flush();
    entityManager.clear();
    
    List<UserGroup> groups = repository.findByOwner(child);
    assertThat(groups.size(), is(equalTo(1)));
    
    assertThat(groups.get(0).getName(), is(equalTo("grandchild")));
    assertThat(groups.get(0).getAncestryPath(), 
        is(equalTo(makePath(parent.getId(), child.getId()))));    
  }

  @Test
  public void testFindDescendants() throws Exception {
    UserGroupEntity parent = EntityUtil.newGroup("parent");
    UserGroupEntity child = EntityUtil.newGroup("child");
    UserGroupEntity grandchild = EntityUtil.newGroup("grandchild");
        
    entityManager.persist(parent);

    child.setOwner(parent);
    entityManager.persist(child);
    
    grandchild.setOwner(child);
    entityManager.persist(grandchild);

    entityManager.flush();
    entityManager.clear();
    
    List<UserGroup> groups = repository.findDescendants(parent);
    assertThat(groups.size(), is(equalTo(2)));
    
    assertThat(groups.get(0).getName(), is(equalTo("child")));
    assertThat(groups.get(0).getAncestryPath(), 
        is(equalTo(makePath(parent.getId()))));    
    
    assertThat(groups.get(1).getName(), is(equalTo("grandchild")));
    assertThat(groups.get(1).getAncestryPath(), 
        is(equalTo(makePath(parent.getId(), child.getId()))));    

  }

  private String makePath(Long... ids) {
    StringBuilder sb = new StringBuilder();
    sb.append(UserGroup.PATH_DELIMITER);
    for (Long id : ids) {
      sb.append(id).append(UserGroup.PATH_DELIMITER);
    }
    return sb.toString();
  }
  
}
