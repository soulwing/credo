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
import static org.hamcrest.Matchers.empty;
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
import org.soulwing.credo.Credential;
import org.soulwing.credo.domain.CredentialCertificateEntity;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.domain.CredentialKeyEntity;
import org.soulwing.credo.domain.TagEntity;
import org.soulwing.credo.domain.UserGroupEntity;
import org.soulwing.credo.domain.UserGroupMemberEntity;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Integration tests for {@link JpaCredentialRepository}.
 *
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class JpaCredentialRepositoryIT {

  @Inject
  private JpaCredentialRepository repository;
  
  @PersistenceContext
  private EntityManager entityManager;
  
  @Inject
  private UserTransaction tx;
  
  @Deployment
  public static Archive<?> createDeployment() {
      return ShrinkWrap.create(WebArchive.class)
          .addPackage(Credential.class.getPackage())
          .addPackage(CredentialEntity.class.getPackage())
          .addPackage(CredentialRepository.class.getPackage())
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
    CredentialEntity credential = EntityUtil.newCredential(group, 
        EntityUtil.newPrivateKey());
    entityManager.persist(group);
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    CredentialEntity actual = entityManager.find(CredentialEntity.class, 
        credential.getId());
    assertThat(actual.getName(), is(equalTo(credential.getName())));
    assertThat(actual.getNote(), is(equalTo(credential.getNote())));
    assertThat(actual.getIssuer(), is(equalTo(credential.getIssuer())));
    assertThat(actual.getExpiration().getTime(), 
        is(equalTo(credential.getExpiration().getTime())));
  }

  @Test
  public void testAddWithTags() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialKeyEntity privateKey = EntityUtil.newPrivateKey();
    CredentialEntity credential = EntityUtil.newCredential(group, privateKey);
    credential.addTag(new TagEntity("tag1"));
    credential.addTag(new TagEntity("tag2"));
    entityManager.persist(group);
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    Credential entity = entityManager.find(CredentialEntity.class, 
        credential.getId());
    assertThat(entity.getName(), is(equalTo(credential.getName())));
    assertThat(entity.getTags().contains(new TagEntity("tag1")), 
        is(equalTo(true)));
    assertThat(entity.getTags().contains(new TagEntity("tag2")), 
        is(equalTo(true)));
  }

  @Test
  public void testAddWithComponents() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialKeyEntity privateKey = EntityUtil.newPrivateKey();
    CredentialCertificateEntity certificate = EntityUtil.newCertificate();
    CredentialCertificateEntity authority = EntityUtil.newCertificate();
    
    CredentialEntity credential = EntityUtil.newCredential(group, privateKey);
    credential.setPrivateKey(privateKey);
    credential.addCertificate(certificate);
    credential.addCertificate(authority);
    
    entityManager.persist(group);
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    
    CredentialEntity actual = entityManager.find(CredentialEntity.class, 
        credential.getId());
    
    assertThat(actual.getName(), is(equalTo(credential.getName())));
    assertThat(actual.getPrivateKey(), is(not(nullValue())));    
    assertThat(actual.getPrivateKey().getEncoded(),
        is(equalTo(privateKey.getEncoded())));
    
    assertThat(actual.getCertificates(), is(not(empty())));

    CredentialCertificateEntity certificateEntity = 
        actual.getCertificates().get(0);
    assertThat(certificateEntity.getSubject(), 
        is(equalTo(certificate.getSubject())));
    assertThat(certificateEntity.getIssuer(), 
        is(equalTo(certificate.getIssuer())));
    assertThat(certificateEntity.getSerialNumber(), 
        is(equalTo(certificate.getSerialNumber())));
    assertThat(certificateEntity.getNotBefore().getTime(), 
        is(equalTo(certificate.getNotBefore().getTime())));
    assertThat(certificateEntity.getNotAfter().getTime(), 
        is(equalTo(certificate.getNotAfter().getTime())));
    assertThat(certificateEntity.getEncoded(), 
        is(equalTo(certificate.getEncoded())));
  }

  @Test
  public void testFindAllByLoginName() throws Exception {
    final String loginName = "someUser";
    final String groupName = "someGroup";
    UserProfileEntity user = EntityUtil.newUser(loginName);
    UserGroupEntity group = EntityUtil.newGroup(groupName);
    UserGroupEntity otherGroup = EntityUtil.newGroup("someOtherGroup");
    UserGroupMemberEntity groupMember = EntityUtil.newGroupMember(user, group);
    CredentialEntity credential = EntityUtil.newCredential(group, 
        EntityUtil.newPrivateKey());
    CredentialEntity otherCredential = EntityUtil.newCredential(otherGroup, 
        EntityUtil.newPrivateKey());
    otherCredential.setName(otherCredential.getName() + "other");
    entityManager.persist(user);
    entityManager.persist(group);
    entityManager.persist(otherGroup);
    entityManager.persist(groupMember);
    entityManager.persist(credential);
    entityManager.persist(otherCredential);
    entityManager.flush();
    entityManager.clear();
    List<Credential> credentials = repository.findAllByLoginName(loginName);
    assertThat(credentials, is(not(nullValue())));
    assertThat(credentials.size(), is(equalTo(1)));
    Credential actual = credentials.get(0);
    assertThat(actual.getName(), is(equalTo(credential.getName())));
  }

  @Test
  public void testFindById() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialEntity credential = EntityUtil.newCredential(group, 
        EntityUtil.newPrivateKey());
    entityManager.persist(group);
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    Credential actual = repository.findById(credential.getId());
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(credential.getName())));
  }

  @Test
  public void testFindAllByOwnerId() throws Exception {
    UserGroupEntity group = EntityUtil.newGroup("someGroup");
    CredentialEntity credential = EntityUtil.newCredential(group, 
        EntityUtil.newPrivateKey());
    entityManager.persist(group);
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    
    List<Credential> credentials = repository.findAllByOwnerId(group.getId());
    assertThat(credentials.size(), is(equalTo(1)));    
    
    Credential actual = credentials.get(0);
    assertThat(actual, is(not(nullValue())));
    assertThat(actual.getName(), is(equalTo(credential.getName())));
  }

}
