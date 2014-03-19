/*
 * File created on Mar 19, 2014 
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

import org.soulwing.credo.service.SigningRequestService;

/**
 * A bean that supports the Create Signing Request interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class CreateSigningRequestBean implements Serializable {

  private static final long serialVersionUID = -9132630420041335985L;

  @Inject
  protected Conversation conversation;
  
  @Inject
  protected SigningRequestService signingRequestService;
  
  @Inject
  protected DelegatingCredentialEditor editor;
  
  @Inject
  protected PasswordFormEditor passwordEditor;
  
  private Long credentialId;

  /**
   * Gets the unique identifier for the credential that will be used as the
   * basis for the signing request.
   * @return credential identifier or {@code null} if none has been set
   */
  public Long getCredentialId() {
    return credentialId;
  }

  /**
   * Sets the unique identifier for the credential that will be used as the
   * basis for the signing request.
   * @param credentialId the credential identifier to set
   */
  public void setCredentialId(Long credentialId) {
    this.credentialId = credentialId;
  }
  
  /**
   * Gets the editor for the signing request.
   * @return editor
   */
  public DelegatingCredentialEditor getEditor() {
    return editor;
  }
  
  /**
   * Gets the editor for the password entry form.
   * @return editor
   */
  public PasswordFormEditor getPasswordEditor() {
    return passwordEditor;
  }
  
  /**
   * An action that is fired when the details view is displayed.
   * <p>
   * If the {@code credentialId} property is set, this method locates the
   * specified credential, uses it to populate the request editor, and 
   * directs the user to the details view.
   * @return outcome ID
   */
  public String findCredential() {
    // TODO
    return null;
  }
  
  /**
   * An action that is fired when the details view is submitted.
   * <p>
   * This method copies the owner property from the request editor to 
   * the password form editor and directs the user to the password entry view.
   * 
   * @return outcome ID
   */
  public String password() {
    return null;
  }
  
  /**
   * An action that is fired when the details view is submitted.
   * <p>
   * This method creates and protects the actual signing request using the
   * contents of the editor.
   * @return outcome ID
   */
  public String prepare() {
    // TODO
    return null;
  }
  
  /**
   * An action that is fired when the confirmation view is submitted.
   * <p>
   * This method makes the signing request persistent.
   * @return outcome ID
   */
  public String save() {
    // TODO
    return null;
  }

  /**
   * An action that is fired when the user clicks the Download button in
   * the success view.
   * <p>
   * This method produces the JSF response containing the signing request
   * content.
   * @return always {@code null}
   */
  public String download() {
    return null;
  }
  
  /**
   * An action that is fired when the user clicks the Cancel button.
   * @return outcome ID
   */
  public String cancel() {
    // TODO
    return null;
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
