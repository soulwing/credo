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

import java.io.IOException;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.credential.NoSuchCredentialException;

/**
 * A {@link DownloadCredentialRequestService} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class DownloadCredentialRequestServiceBean implements DownloadCredentialRequestService {

  @Inject
  protected CredentialRequestRepository requestRepository;
  
  @Inject
  protected CreateCredentialRequestService createRequestService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void downloadRequest(Long requestId, FileDownloadResponse response)
      throws NoSuchCredentialException, IOException {
    CredentialRequest request = requestRepository.findById(requestId);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    createRequestService.downloadRequest(request, response);
  }

}
