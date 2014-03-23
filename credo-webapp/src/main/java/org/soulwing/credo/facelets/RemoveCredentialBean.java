/*
 * File created on Mar 17, 2014 
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
package org.soulwing.credo.facelets;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.Credential;
import org.soulwing.credo.service.CredentialService;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;

/**
 * A bean that supports the Remove Credential interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class RemoveCredentialBean implements Serializable {

  private static final long serialVersionUID = 8209629154531518443L;

  static final String SUCCESS_OUTCOME_ID = "success";
  static final String FAILURE_OUTCOME_ID = "failed";
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  @Inject
  protected CredentialService credentialService;
  
  @Inject
  protected Conversation conversation;
  
  @Inject
  protected Errors errors;
  
  private Long id;
  private Credential credential;
  
  /**
   * Gets the unique identifier for the credential to be removed.
   * @return unique identifier
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier for the credential to be removed.
   * @param id the unique identifier to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the credential to be removed.
   * @return credential
   */
  public Credential getCredential() {
    return credential;
  }

  /**
   * An action that is invoked when the confirmation form is loaded.
   * <p>
   * This action is responsible for locating the credential to be removed.
   * @return outcome ID
   */
  public String findCredential() {
    if (id == null) {
      errors.addError("id", "credentialIdIsRequired");
      return FAILURE_OUTCOME_ID;
    }
    try {
      credential = credentialService.findCredentialById(id);
      beginConversation();
      return null;
    }
    catch (NoSuchCredentialException ex) {
      errors.addError("id", "credentialNotFound", id);
      return FAILURE_OUTCOME_ID;
    }
  }
  
  /**
   * An action that is invoked when the user cancels the request to remove
   * the selected credential.
   * @return
   */
  public String cancel() {
    endConversation();
    return CANCEL_OUTCOME_ID;
  }
  
  /**
   * An action that is invoked when the user confirms the request to remove
   * the selected credential.
   * @return outcome ID
   */
  public String remove() {
    try {
      credentialService.removeCredential(id);
      return SUCCESS_OUTCOME_ID;
    }
    catch (GroupAccessException ex) {
      errors.addError("groupAccessDenied", 
          new Object[] { ex.getGroupName() });
      return FAILURE_OUTCOME_ID;
    }
    finally {
      endConversation();
    }
  }
  
  private void beginConversation() {
    if (!conversation.isTransient()) return;
    conversation.begin();
  }
  
  private void endConversation() {
    if (conversation.isTransient()) return;
    conversation.end();
  }

}
