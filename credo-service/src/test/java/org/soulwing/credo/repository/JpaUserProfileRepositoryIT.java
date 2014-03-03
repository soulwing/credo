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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Integration tests for {@link JpaUserProfileRepository}.
 *
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class JpaUserProfileRepositoryIT {

  @Inject
  private JpaUserProfileRepository repository;
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Inject
  private UserTransaction tx;
  
  @Deployment
  public static Archive<?> createDeployment() {
      return ShrinkWrap.create(WebArchive.class)
          .addPackage(UserProfile.class.getPackage())
          .addPackage(UserProfileEntity.class.getPackage())
          .addPackage(UserProfileRepository.class.getPackage())
          .addAsResource("persistence-test.xml", "META-INF/persistence.xml")
          .addAsResource("META-INF/orm.xml", "META-INF/orm.xml")
          .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }
  
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
    UserProfileEntity profile = newUserProfile();
    repository.add(profile);
    entityManager.flush();
    entityManager.clear();
    UserProfileEntity entity = entityManager.find(UserProfileEntity.class, 
        profile.getId());
    
    assertThat(entity.getLoginName(), is(equalTo(profile.getLoginName())));
    assertThat(entity.getFullName(), is(equalTo(profile.getFullName())));
    assertThat(entity.getPassword(), is(equalTo(profile.getPassword())));
    assertThat(entity.getPublicKey(), is(equalTo(profile.getPublicKey())));
    assertThat(entity.getPrivateKey(), is(equalTo(profile.getPrivateKey())));
    assertThat(entity.getDateCreated(), is(not(nullValue())));
    assertThat(entity.getDateModified(), is(equalTo(entity.getDateCreated())));
  }

  @Test(expected = PersistenceException.class)
  public void testAddWithDuplicateLoginName() throws Exception {
    repository.add(newUserProfile());
    repository.add(newUserProfile());
    entityManager.flush();
  }

  @Test
  public void testFindByLoginName() throws Exception {
    UserProfileEntity expected = newUserProfile();
    repository.add(expected);
    entityManager.flush();
    entityManager.clear();
    
    UserProfile actual = repository.findByLoginName(expected.getLoginName());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getLoginName(), is(equalTo(expected.getLoginName())));
  }

  @Test
  public void testFindByLoginNameNotFound() throws Exception {
    UserProfile actual = repository.findByLoginName("doesNotExist");
    assertThat(actual, is(nullValue()));
  }

  private UserProfileEntity newUserProfile() {
    UserProfileEntity profile = new UserProfileEntity();
    profile.setLoginName("loginName");
    profile.setFullName("fullName");
    profile.setPassword("password");
    profile.setPublicKey("publicKey");
    profile.setPrivateKey("privateKey");
    return profile;
  }

}
