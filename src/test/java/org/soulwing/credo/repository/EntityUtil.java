/*
 * File created on Mar 5, 2014 
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

import org.soulwing.credo.domain.CredentialCertificateEntity;
import org.soulwing.credo.domain.CredentialCertificationRequestEntity;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.domain.CredentialKeyEntity;
import org.soulwing.credo.domain.CredentialRequestEntity;
import org.soulwing.credo.domain.UserGroupEntity;
import org.soulwing.credo.domain.UserGroupMemberEntity;
import org.soulwing.credo.domain.UserProfileEntity;

/**
 * Static helper methods for creating entity classes.
 *
 * @author Carl Harris
 */
public class EntityUtil {

  public static UserProfileEntity newUser(String loginName) {
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

  public static UserGroupEntity newGroup(String name) {
    UserGroupEntity group = new UserGroupEntity();
    Date now = new Date();
    group.setName(name);
    group.setDateCreated(now);
    group.setDateModified(now);
    return group;
  }

  public static UserGroupMemberEntity newGroupMember(UserProfileEntity user,
      UserGroupEntity group) {
    UserGroupMemberEntity groupMember = new UserGroupMemberEntity();
    Date now = new Date();
    groupMember.setUser(user);
    groupMember.setGroup(group);
    groupMember.setSecretKey("some secret key");
    groupMember.setDateCreated(now);
    return groupMember;
  }

  public static CredentialEntity newCredential(UserGroupEntity group,
      CredentialKeyEntity privateKey) {
    CredentialEntity credential = new CredentialEntity();
    Date now = new Date();
    credential.setName("Test");
    credential.setOwner(group);
    credential.setNote("This is a test.");
    credential.setIssuer("TestIssuer");
    credential.setExpiration(new Date());
    credential.setPrivateKey(privateKey);
    credential.setDateCreated(now);
    credential.setDateModified(now);
    return credential;
  }

  public static CredentialKeyEntity newPrivateKey() {
    CredentialKeyEntity privateKey = new CredentialKeyEntity();
    privateKey.setEncoded("testContent");
    return privateKey;
  }

  public static CredentialCertificateEntity newCertificate() {
    CredentialCertificateEntity certificate = new CredentialCertificateEntity();
    certificate.setSubject("testSubject");
    certificate.setIssuer("testIssuer");
    certificate.setNotBefore(new Date(0));
    certificate.setNotAfter(new Date(1));
    certificate.setSerialNumber("testSerialNumber");
    certificate.setEncoded("testContent");
    return certificate;
  }

  public static CredentialRequestEntity newRequest(UserGroupEntity owner,
      CredentialKeyEntity privateKey, 
      CredentialCertificationRequestEntity certificationRequest) {
    
    CredentialRequestEntity request = new CredentialRequestEntity();
    Date now = new Date();
    request.setName("testName");
    request.setSubject("testSubject");
    request.setNote("testNote");
    request.setDateCreated(now);
    request.setDateModified(now);
    request.setOwner(owner);
    request.setPrivateKey(privateKey);
    request.setCertificationRequest(certificationRequest);
    return request;
  }

  public static CredentialCertificationRequestEntity newCertificationRequest() {
    CredentialCertificationRequestEntity certificationRequest =
        new CredentialCertificationRequestEntity();
    certificationRequest.setEncoded("testCertificationRequest");
    return certificationRequest;
  }

}
