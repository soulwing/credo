/*
 * File created on Apr 12, 2014 
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

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;

/**
 * A {@link RemoveCredentialRequestService} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RemoveCredentialRequestServiceBean implements RemoveCredentialRequestService {

  @Inject
  protected CredentialRepository credentialRepository;

  @Inject
  protected CredentialRequestRepository requestRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequestDetail findRequestById(Long id)
      throws NoSuchCredentialException {
    
    CredentialRequest request = requestRepository.findById(id);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    
    Credential credential = credentialRepository.findByRequestId(id);
    
    CredentialRequestWrapper detail = new CredentialRequestWrapper(request);
    detail.setCredentialCreated(credential != null);
    
    return detail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void removeRequest(Long id) throws GroupAccessException {
    try {
      Credential credential = credentialRepository.findByRequestId(id);
      if (credential != null) {
        credential.setRequest(null);
        credentialRepository.update(credential);
      }
      CredentialRequest request = requestRepository.findById(id);
      if (request != null) {
        requestRepository.remove(request, credential == null);
      }
    }
    catch (OwnerAccessControlException ex) {
      throw new GroupAccessException(ex.getGroupName());
    }
  }
  
}
