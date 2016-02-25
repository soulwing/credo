/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.service.request;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.service.UserContextService;

/**
 * A concrete {@link CredentialRequestService} as a singleton session bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class CredentialRequestServiceBean 
    implements CredentialRequestService {

  @Inject
  protected CredentialRequestRepository requestRepository;

  @Inject
  protected UserGroupRepository groupRepository;
  
  @Inject
  protected UserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public List<CredentialRequest> findAllRequests() {
    List<UserGroup> allGroups = new ArrayList<>();
    List<UserGroup> groups = groupRepository.findByLoginName(
        userContextService.getLoginName());
    allGroups.addAll(groups);
    for (UserGroup group : groups) {
      allGroups.addAll(groupRepository.findDescendants(group));
    }
    return requestRepository.findAllByOwners(allGroups);
  }

}
