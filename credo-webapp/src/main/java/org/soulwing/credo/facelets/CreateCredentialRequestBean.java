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

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.request.CreateCredentialRequestService;
import org.soulwing.credo.service.request.CredentialRequestException;

/**
 * A bean that supports the Create Credential Request interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class CreateCredentialRequestBean implements Serializable {

  private static final long serialVersionUID = -9132630420041335985L;

  static final String SUBJECT_OUTCOME_ID = "index";
  
  static final String DETAILS_OUTCOME_ID = "details";
  
  static final String CONFIRM_OUTCOME_ID = "confirm";
  
  static final String PASSWORD_OUTCOME_ID = "password";
  
  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  static final String FAILURE_OUTCOME_ID = "failure";
  
  /** conversation timeout (milliseconds) */
  static final long CONVERSATION_TIMEOUT = 1800000;

  @Inject
  protected Conversation conversation;
  
  @Inject
  protected CreateCredentialRequestService requestService;
  
  @Inject
  protected DelegatingCredentialRequestEditor editor;
  
  @Inject
  protected PasswordFormEditor passwordEditor;
  
  @Inject
  protected Errors errors;
  
  @Inject
  protected FacesContext facesContext;
  
  private Long credentialId;
  private CredentialRequest signingRequest;

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
  public DelegatingCredentialRequestEditor getEditor() {
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
   * Gets the prepared signing request.
   * <p>
   * @return signing request or {@code null} if the request has not been
   *    prepared
   */
  CredentialRequest getSigningRequest() {
    return signingRequest;
  }

  /**
   * Sets the prepared signing request.
   * <p>
   * This method is exposed to support unit testing.
   * @param signingRequest the signing request to set
   */
  void setSigningRequest(CredentialRequest signingRequest) {
    this.signingRequest = signingRequest;
  }
  
  /**
   * Gets the prepared certification request.
   * @return
   */
  public String getCertificationRequest() {
    if (signingRequest == null) return null;
    return signingRequest.getCertificationRequest().getContent();
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
    try {
      if (credentialId == null) {
        editor.setDelegate(requestService.createEditor());
        return SUBJECT_OUTCOME_ID;
      }  
      try {
        editor.setDelegate(
            requestService.createEditor(credentialId, errors));
        return DETAILS_OUTCOME_ID;
      }
      catch (NoSuchCredentialException ex) {
        return FAILURE_OUTCOME_ID;
      }
    }
    finally {
      if (editor.getDelegate() != null) {
        beginConversation();
      }
    }
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
    passwordEditor.setGroupName(editor.getOwner());
    return PASSWORD_OUTCOME_ID;
  }
  
  /**
   * An action that is fired when the details view is submitted.
   * <p>
   * This method creates and protects the actual signing request using the
   * contents of the editor.
   * @return outcome ID
   */
  public String prepare() {
    try {
      signingRequest = requestService.createRequest(
          editor.getDelegate(), passwordEditor, errors);
      return CONFIRM_OUTCOME_ID;      
    }
    catch (PassphraseException ex) {
      return PASSWORD_OUTCOME_ID;
    }
    catch (GroupAccessException|NoSuchGroupException ex) {
      return DETAILS_OUTCOME_ID;
    }
    catch (CredentialRequestException ex) {
      endConversation();
      return FAILURE_OUTCOME_ID;
    }
  }
  
  /**
   * An action that is fired when the confirmation view is submitted.
   * <p>
   * This method makes the signing request persistent.
   * @return outcome ID
   */
  public String save() {
    try {
      requestService.saveRequest(signingRequest, errors);
      return CreateCredentialRequestBean.SUCCESS_OUTCOME_ID;
    }
    catch (GroupAccessException ex) {
      return DETAILS_OUTCOME_ID;
    }
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
    try {
      requestService.downloadRequest(signingRequest, 
          new FacesFileDownloadResponse(facesContext));
      facesContext.responseComplete();
      return null;
    }
    catch (IOException ex) {
      // FIXME
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * An action that is fired when the user clicks the Cancel button.
   * @return outcome ID
   */
  public String cancel() {
    endConversation();
    return CANCEL_OUTCOME_ID;
  }
  
  private void beginConversation() {
    if (!conversation.isTransient()) return;
    conversation.begin();
    conversation.setTimeout(CONVERSATION_TIMEOUT);
  }
  
  private void endConversation() {
    if (conversation.isTransient()) return;
    conversation.end();
  }

}
