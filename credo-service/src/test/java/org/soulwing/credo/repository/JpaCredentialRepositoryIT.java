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

import java.util.Date;
import java.util.List;

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
import org.soulwing.credo.Credential;
import org.soulwing.credo.domain.CredentialCertificateEntity;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.domain.CredentialKeyEntity;
import org.soulwing.credo.domain.TagEntity;

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
    CredentialEntity credential = new CredentialEntity();
    credential.setName("Test");
    credential.setNote("This is a test.");
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    CredentialEntity entity = entityManager.find(CredentialEntity.class, 
        credential.getId());
    assertThat(entity.getName(), is(equalTo(credential.getName())));
  }

  @Test(expected = PersistenceException.class)
  public void testAddWithDuplicateName() throws Exception {
    CredentialEntity credential = new CredentialEntity();
    credential.setName("Test");
    credential.setNote("This is a test.");
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    credential = new CredentialEntity();
    credential.setName("Test");
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  public void testAddWithTags() throws Exception {
    CredentialEntity credential = new CredentialEntity();
    credential.setName("Test");
    credential.addTag(new TagEntity("tag1"));
    credential.addTag(new TagEntity("tag2"));
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
    CredentialKeyEntity privateKey = newPrivateKey();
    CredentialCertificateEntity certificate = newCertificate();
    CredentialCertificateEntity authority = newCertificate();
    
    CredentialEntity credential = new CredentialEntity();
    credential.setName("Test");
    credential.setPrivateKey(privateKey);
    credential.addCertificate(certificate);
    credential.addCertificate(authority);
    
    repository.add(credential);
    entityManager.flush();
    entityManager.clear();
    
    CredentialEntity credentialEntity = entityManager.find(CredentialEntity.class, 
        credential.getId());
    
    assertThat(credentialEntity.getName(), is(equalTo(credential.getName())));
    assertThat(credentialEntity.getPrivateKey(), is(not(nullValue())));    
    assertThat(credentialEntity.getPrivateKey().getEncoded(),
        is(equalTo(privateKey.getEncoded())));
    
    assertThat(credentialEntity.getCertificates(), is(not(empty())));

    CredentialCertificateEntity certificateEntity = 
        credentialEntity.getCertificates().get(0);
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
  public void testFindAll() throws Exception {
    CredentialEntity entity = new CredentialEntity();
    entity.setName("Test");
    entity.addTag(new TagEntity("tag"));
    repository.add(entity);
    entityManager.flush();
    entityManager.clear();
    List<Credential> credentials = repository.findAll();
    assertThat(credentials, is(not(nullValue())));
    assertThat(credentials, is(not(empty())));
    Credential credential = credentials.get(0);
    assertThat(credential.getName(), is(equalTo(entity.getName())));
  }

  private CredentialKeyEntity newPrivateKey() {
    CredentialKeyEntity privateKey = new CredentialKeyEntity();
    privateKey.setEncoded("testContent");
    return privateKey;
  }

  private CredentialCertificateEntity newCertificate() {
    CredentialCertificateEntity certificate = new CredentialCertificateEntity();
    certificate.setSubject("testSubject");
    certificate.setIssuer("testIssuer");
    certificate.setNotBefore(new Date(0));
    certificate.setNotAfter(new Date(1));
    certificate.setSerialNumber("testSerialNumber");
    certificate.setEncoded("testContent");
    return certificate;
  }
  
}
