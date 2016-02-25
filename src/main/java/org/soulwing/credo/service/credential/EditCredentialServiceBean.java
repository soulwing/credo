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
package org.soulwing.credo.service.credential;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Credential;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.request.EditCredentialRequestService;

/**
 * A {@link EditCredentialRequestService} implemented as a simple bean.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EditCredentialServiceBean
    implements EditCredentialService {

  @Inject
  protected CredentialRepository credentialRepository;
  
  @Inject
  protected CredentialEditorFactory editorFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public CredentialEditor editCredential(Long id)
      throws NoSuchCredentialException {
    
    Credential credential = credentialRepository.findById(id);
    if (credential == null) {
      throw new NoSuchCredentialException();
    }
    
    return editorFactory.newEditor(credential);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void saveCredential(CredentialEditor editor, Errors errors)
      throws CredentialException, NoSuchCredentialException,
      GroupAccessException, PassphraseException, MergeConflictException {
    
    Validate.isTrue(editor instanceof SaveableCredentialEditor);
    ((SaveableCredentialEditor) editor).save(errors);
  }
  
}
