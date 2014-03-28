/*
 * File created on Feb 13, 2014 
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

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportService;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

/**
 * A bean that supports the Import Signed Certificate interaction.
 * 
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class ImportSignedCertificateBean implements Serializable {

  public enum OwnerStatus {
    NONE, EXISTS, WILL_CREATE, INACCESSIBLE
  }

  static final String RESTART_OUTCOME_ID = "index";
  
  static final String DETAILS_OUTCOME_ID = "details";

  static final String CONFIRM_OUTCOME_ID = "confirm";

  static final String FAILURE_OUTCOME_ID = "failure";

  static final String SUCCESS_OUTCOME_ID = "success";

  static final String CANCEL_OUTCOME_ID = "cancel";

  static final String PASSWORD1_OUTCOME_ID = "password1";

  static final String PASSWORD2_OUTCOME_ID = "password2";
  
  static final String CLEANUP_OUTCOME_ID = "cleanup";
  
  static final String CLEANUP_FAILURE_OUTCOME_ID = "cleanupFailure";

  private static final long serialVersionUID = -5565484780336702769L;

  @Inject
  protected Conversation conversation;

  @Inject
  protected Errors errors;

  @Inject
  protected ImportService importService;

  @Inject
  protected FacesContext facesContext;

  @Inject
  protected FileUploadEditor fileUploadEditor;
  
  @Inject
  protected DelegatingCredentialEditor<ImportDetails> editor;

  @Inject
  protected PasswordFormEditor passwordEditor;

  private Long requestId;
  private CredentialRequest request;
  private Credential credential;
  private boolean removeRequest = true;
 
  /**
   * Gets the unique identifier of the credential request that will be used
   * as the basis for this import.
   * @return request ID
   */
  public Long getRequestId() {
    return requestId;
  }

  /**
   * Sets the unique identifier of the credential request that will be used
   * as the basis for this import.
   * @param requestId the request ID to set
   */
  public void setRequestId(Long requestId) {
    this.requestId = requestId;
  }

  /**
   * Gets the supporting editor bean for the file upload form.
   * @return editor
   */
  public FileUploadEditor getFileUploadEditor() {
    return fileUploadEditor;
  }
  
  /**
   * Gets the supporting bean for the password entry form.
   * @return editors
   */
  public PasswordFormEditor getPasswordEditor() {
    return passwordEditor;
  }

  /**
   * Gets the credential editor.
   * @return editor
   */
  public DelegatingCredentialEditor getEditor() {
    return editor;
  }
  
  /**
   * Gets a flag that determines whether the request will be removed upon
   * successful creation of the credential.
   * @return {@code true} if the request should be removed
   */
  public boolean isRemoveRequest() {
    return removeRequest;
  }

  /**
   * Gets a flag that determines whether the request will be removed upon
   * successful creation of the credential.
   * @param removeRequest the flag state to set
   */
  public void setRemoveRequest(boolean removeRequest) {
    this.removeRequest = removeRequest;
  }

  /**
   * Gets the credential request.
   * @return credential request
   */
  public CredentialRequest getRequest() {
    return request;
  }

  /**
   * Sets the credential request.
   * <p>
   * This method is exposed to support unit testing.
   * @param request the credential request
   */
  void setRequest(CredentialRequest request) {
    this.request = request;
  }

  /**
   * Gets the details produced by the import service.
   * <p>
   * This method is exposed to support unit testing.
   * @return details
   */
  ImportDetails getDetails() {
    return (ImportDetails) editor.getDelegate();
  }
  
  /**
   * Sets the details produced by the import service.
   * <p>
   * This method is exposed to support unit testing.
   * @param details the details to set
   */
  void setDetails(ImportDetails details) {
    editor.setDelegate(details);
  }

  /**
   * Gets the credential that was produced by the import service.
   * <p>
   * This method is exposed principally to support unit testing.
   * @return credential
   */
  Credential getCredential() {
    return credential;
  }

  /**
   * Sets the credential.
   * <p>
   * This method is exposed principally to support unit testing.
   * @param credential the credential to set
   */
  void setCredential(Credential credential) {
    this.credential = credential;
  }

  /**
   * Finds the credential request on which this import will be based.
   * @return outcome ID
   */
  public String findRequest() {
    if (requestId == null) {
      errors.addError("requestId", "requestIdIsRequired");
      return FAILURE_OUTCOME_ID;
    }
    try {
      request = importService.findRequestById(requestId);
      beginConversation();
      return null;
    }
    catch (NoSuchCredentialException ex) {
      errors.addError("requestId", "requestNotFound", requestId);
      return FAILURE_OUTCOME_ID;
    }
  }
  
  /**
   * Action that is fired after the upload form is submitted.
   * @return outcome ID
   */
  public String password1() {
    try {
      // make sure uploaded content is held in the conversation
      fileUploadEditor.fileList();
      
      passwordEditor.setGroupName(request.getOwner().getName());
      return ImportSignedCertificateBean.PASSWORD1_OUTCOME_ID;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Action that is fired when first password form is submitted.
   * @return outcome ID
   */
  public String prepare() {
    try {
      ImportDetails details = importService.prepareImport(request, 
          fileUploadEditor.fileList(), passwordEditor, errors);
      editor.setDelegate(details);
      return DETAILS_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return RESTART_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      return null;
    }
    catch (GroupAccessException ex) {
      return FAILURE_OUTCOME_ID;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Action that is fired after the details form is submitted.
   * @return outcome ID
   */
  public String password2() {
    passwordEditor.setGroupName(editor.getOwner());
    return ImportSignedCertificateBean.PASSWORD2_OUTCOME_ID;
  }

  /**
   * Action that is fired when the second password form is submitted.
   * @return outcome ID
   */
  public String protect() {
    try {
      credential = importService.createCredential(
          (ImportDetails) editor.getDelegate(), passwordEditor, errors);
      return CONFIRM_OUTCOME_ID;
    }
    catch (GroupAccessException ex) {
      return DETAILS_OUTCOME_ID;
    }
    catch (NoSuchGroupException ex) {
      return DETAILS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      return null;
    }
  }

  /**
   * Action that is fired when the confirmation form is submitted.
   * @return outcome ID
   */
  public String save() {
    try {
      credential.setRequest(request);
      importService.saveCredential(credential, removeRequest, errors);
      if (request.getCredential() == null) {
        endConversation();
        return SUCCESS_OUTCOME_ID;
      }
      return CLEANUP_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return null;
    }
    catch (GroupAccessException ex) {
      return DETAILS_OUTCOME_ID;
    }
  }

  /**
   * Action that is fired when any form in the interaction is canceled.
   * @return outcome ID
   */
  public String cancel() {
    endConversation();
    return CANCEL_OUTCOME_ID;
  }

  /**
   * Action that is fired when the user chooses to remove the existing 
   * credential that the new credential is intended to replace.
   * @return outcome ID
   */
  public String cleanup() {
    try {
      importService.removeCredential(request.getCredential(), errors);
      endConversation();
      return SUCCESS_OUTCOME_ID;
    }
    catch (GroupAccessException ex) {
      return CLEANUP_FAILURE_OUTCOME_ID;
    }
  }
  
  private void beginConversation() {
    if (conversation.isTransient()) {
      conversation.begin();
    }
  }

  private void endConversation() {
    if (!conversation.isTransient()) {
      conversation.end();
    }
  }

}
