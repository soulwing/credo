/*
 * File created on Apr 13, 2014 
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

import org.apache.commons.lang.Validate;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;

/**
 * A {@link EditCredentialRequestService} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EditCredentialRequestServiceBean
    implements EditCredentialRequestService {

  @Inject
  protected CredentialRequestRepository requestRepository;
  
  @Inject
  protected CredentialRequestEditorFactory editorFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialRequestEditor editRequest(Long id)
      throws NoSuchCredentialException {
    
    CredentialRequest request = requestRepository.findById(id);
    if (request == null) {
      throw new NoSuchCredentialException();
    }
    
    return editorFactory.newEditor(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveRequest(CredentialRequestEditor editor,
      Errors errors)
      throws CredentialRequestException, NoSuchCredentialException,
      GroupAccessException, PassphraseException, MergeConflictException {
    
    Validate.isTrue(editor instanceof SaveableRequestEditor);
    ((SaveableRequestEditor) editor).save(errors);
  }
  
}
