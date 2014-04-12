/*
 * File created on Feb 21, 2014 
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
package org.soulwing.credo.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.security.OwnerAccessControlException;

/**
 * A concrete {@link CredentialService} implementation.
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConcreteCredentialService implements CredentialService {

  @Inject
  protected CredentialRepository credentialRepository;
  
  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Credential findCredentialById(Long id) 
      throws NoSuchCredentialException{
    Credential credential = credentialRepository.findById(id);
    if (credential == null) {
      throw new NoSuchCredentialException();
    }
    return credential;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Credential> findAllCredentials() {
    List<UserGroup> allGroups = new ArrayList<>();
    List<UserGroup> groups = groupRepository.findByLoginName(
        userContextService.getLoginName());
    allGroups.addAll(groups);
    for (UserGroup group : groups) {
      allGroups.addAll(groupRepository.findDescendants(group));
    }
    return credentialRepository.findAllByOwners(allGroups);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeCredential(Long id) throws GroupAccessException {
    try {
      Credential credential = credentialRepository.findById(id);
      if (credential != null) {
        credentialRepository.remove(credential);
      }
    }
    catch (OwnerAccessControlException ex) {
      throw new GroupAccessException(ex.getGroupName());
    }
  }
  
}
