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

import java.util.Arrays;
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
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.domain.CredentialKeyEntity;
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
  public void testAddWithCredential() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    Credential credential = EntityUtil.newCredential(group,  
        EntityUtil.newPrivateKey());
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(),
        EntityUtil.newCertificationRequest());

    request.setCredential(credential);
    entityManager.persist(group);
    entityManager.persist(credential);
    repository.add(request);
    entityManager.flush();
    entityManager.clear();
    
    CredentialRequestEntity actual = entityManager.find(
        CredentialRequestEntity.class, request.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getCredential().getId(), 
        is(equalTo(credential.getId())));
  }

  @Test
  public void testRemove() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(),
        EntityUtil.newCertificationRequest());

    entityManager.persist(group);
    repository.add(request);
    entityManager.flush();
    entityManager.clear();
    
    CredentialRequest actual = repository.findById(request.getId());   
    assertThat(actual, is(not(nullValue())));

    request = entityManager.merge(request);
    repository.remove(request, true);
    assertThat(repository.findById(request.getId()), is(nullValue()));
  }

  @Test
  public void testRemoveLeavingPrivateKey() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialKeyEntity privateKey = EntityUtil.newPrivateKey();
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        privateKey, EntityUtil.newCertificationRequest());

    CredentialEntity credential = EntityUtil.newCredential(group, privateKey);
    credential.setRequest(request);
    entityManager.persist(group);
    repository.add(request);
    entityManager.persist(credential);
    
    entityManager.flush();
    entityManager.clear();
    
    CredentialRequest actual = repository.findById(request.getId());   
    assertThat(actual, is(not(nullValue())));

    credential.setRequest(null);
    credential = entityManager.merge(credential);
    
    request = entityManager.merge(request);
    repository.remove(request, false);
    assertThat(repository.findById(request.getId()), is(nullValue()));
    
    CredentialEntity actualCredential = entityManager.find(
        CredentialEntity.class, credential.getId());
    assertThat(actualCredential, is(not(nullValue())));
    assertThat(actualCredential.getPrivateKey(), is(not(nullValue())));
  }


  @Test
  public void testFindById() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(),
        EntityUtil.newCertificationRequest());

    entityManager.persist(group);
    repository.add(request);
    entityManager.flush();
    entityManager.clear();
    
    CredentialRequest actual = repository.findById(request.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getId(), is(equalTo(request.getId())));
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

  @Test
  public void testFindAllByOwners() throws Exception {
    UserGroupEntity group1 = EntityUtil.newGroup("group1");
    UserGroupEntity group2 = EntityUtil.newGroup("group2");
    CredentialRequestEntity request1 = EntityUtil.newRequest(group1, 
        EntityUtil.newPrivateKey(), EntityUtil.newCertificationRequest());
    CredentialRequestEntity request2 = EntityUtil.newRequest(group2, 
        EntityUtil.newPrivateKey(), EntityUtil.newCertificationRequest());

    request1.setName("request1");
    request2.setName("request2");
    request1.setOwner(group1);
    request2.setOwner(group2);
    
    entityManager.persist(group1);
    entityManager.persist(group2);    
    repository.add(request1);
    repository.add(request2);
    
    entityManager.flush();
    entityManager.clear();
    
    List<UserGroup> owners = Arrays.asList(new UserGroup[] { group1, group2 });
    List<CredentialRequest> credentials = repository.findAllByOwners(owners);
    assertThat(credentials.size(), is(equalTo(2)));    
    
    CredentialRequest actual1 = credentials.get(0);
    assertThat(actual1.getId(), is(equalTo(request1.getId())));
    
    CredentialRequest actual2 = credentials.get(1);
    assertThat(actual2.getId(), is(equalTo(request2.getId())));
  }

  @Test
  public void testFindAllByOwnerId() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialRequestEntity request = EntityUtil.newRequest(group, 
        EntityUtil.newPrivateKey(),
        EntityUtil.newCertificationRequest());
    entityManager.persist(group);
    repository.add(request);
    entityManager.flush();
    entityManager.clear();
    
    List<CredentialRequest> requests = repository.findAllByOwnerId(group.getId());
    assertThat(requests.size(), is(equalTo(1)));    
    
    CredentialRequest actual = requests.get(0);
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(request.getName())));
  }



}
