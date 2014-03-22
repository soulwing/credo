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
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.domain.CredentialRequestEntity;
import org.soulwing.credo.domain.TagEntity;
import org.soulwing.credo.domain.UserGroupEntity;
import org.soulwing.credo.domain.UserGroupMemberEntity;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Integration tests for {@link JpaCredentialRequestRepository}.
 *
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class JpaCredentialRequestRepositoryIT {

  private static final TagEntity TAG1 = new TagEntity("tag1");

  private static final TagEntity TAG2 = new TagEntity("tag2");

  @Inject
  private JpaCredentialRequestRepository repository;
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Inject
  private UserTransaction tx;
  
  @Deployment
  public static Archive<?> createDeployment() {
      return ShrinkWrap.create(WebArchive.class)
          .addPackage(CredentialRequest.class.getPackage())
          .addPackage(CredentialRequestEntity.class.getPackage())
          .addPackage(CredentialRequestRepository.class.getPackage())
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
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(),
        EntityUtil.newCertificationRequest());
    request.addTag(TAG1);
    request.addTag(TAG2);
    entityManager.persist(group);
    repository.add(request);
    entityManager.flush();
    entityManager.clear();
    
    CredentialRequestEntity actual = entityManager.find(
        CredentialRequestEntity.class, request.getId());
    assertThat(actual.getName(), is(equalTo(request.getName())));
    assertThat(actual.getNote(), is(equalTo(request.getNote())));
    assertThat(actual.getSubject(), is(equalTo(request.getSubject())));
    assertThat(actual.getPrivateKey().getContent(), 
        is(equalTo(request.getPrivateKey().getContent())));
    assertThat(actual.getCertificationRequest().getContent(),
        is(equalTo(request.getCertificationRequest().getContent())));
    assertThat(actual.getTags().contains(TAG1), is(true)); 
    assertThat(actual.getTags().contains(TAG2), is(true)); 
  }

  @Test
  public void testFindAllByLoginName() throws Exception {
    final String loginName = "someUser";
    final String groupName = "someGroup";
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    UserGroupEntity otherGroup = EntityUtil.newGroup("someOtherGroup");
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(), EntityUtil.newCertificationRequest());
    CredentialRequestEntity otherRequest = EntityUtil.newRequest(otherGroup, 
        EntityUtil.newPrivateKey(), EntityUtil.newCertificationRequest());
    otherRequest.setName(otherRequest.getName() + "other");
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(otherGroup);
    entityManager.persist(groupMember);
    entityManager.persist(request);
    entityManager.persist(otherRequest);
    entityManager.flush();
    entityManager.clear();
    List<CredentialRequest> requests = repository.findAllByLoginName(loginName);
    assertThat(requests, is(not(nullValue())));
    assertThat(requests.size(), is(equalTo(1)));
    CredentialRequest actual = requests.get(0);
    assertThat(actual.getName(), is(equalTo(actual.getName())));
  }

}
