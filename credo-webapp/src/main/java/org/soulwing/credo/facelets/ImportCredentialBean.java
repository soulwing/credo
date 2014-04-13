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
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.credential.ImportDetails;
import org.soulwing.credo.service.credential.ImportException;
import org.soulwing.credo.service.credential.ImportService;

/**
 * A bean that supports the Import Credential interaction.
 * 
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class ImportCredentialBean implements Serializable {

  public enum OwnerStatus {
    NONE, EXISTS, WILL_CREATE, INACCESSIBLE
  }

  static final String DETAILS_OUTCOME_ID = "details";

  static final String CONFIRM_OUTCOME_ID = "confirm";

  static final String WARNINGS_OUTCOME_ID = "warnings";

  static final String FAILURE_OUTCOME_ID = "failure";

  static final String SUCCESS_OUTCOME_ID = "success";

  static final String CANCEL_OUTCOME_ID = "cancel";

  static final String PASSPHRASE_OUTCOME_ID = "passphrase";
  
  static final String PASSWORD_OUTCOME_ID = "password";

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

  private Password passphrase;
  private Credential credential;


  /**
   * Gets the file upload editor component.
   * @return editor
   */
  public FileUploadEditor getFileUploadEditor() {
    return fileUploadEditor;
  }

  /**
   * Gets the private key passphrase.
   * @return passphrase
   */
  public Password getPassphrase() {
    return passphrase;
  }

  /**
   * Sets the private key passphrase.
   * @param passphrase the passphrase to set
   */
  public void setPassphrase(Password passphrase) {
    this.passphrase = passphrase;
  }

  /**
   * Gets the supporting bean for the password entry form.
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
   * Action that is fired when the form containing the files to import has been
   * submitted.
   * @return outcome ID
   */
  public String upload() {
    if (conversation.isTransient()) {
      conversation.begin();
    }
    try {
      ImportDetails details = importService.prepareImport(
          fileUploadEditor.fileList(), passphrase, errors);
      editor.setDelegate(details);
      return DETAILS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      return PASSPHRASE_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return null;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Action that is fired when the form containing the credential details
   * is submitted.
   * @return outcome ID
   */
  public String password() {
    passwordEditor.setGroupName(editor.getOwner());
    return ImportCredentialBean.PASSWORD_OUTCOME_ID;
  }
  
  /**
   * Action that is fired when the password form is submitted.
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
      importService.saveCredential(credential, false, errors);
      conversation.end();
      return SUCCESS_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return null;
    }
    catch (GroupAccessException ex) {
      return FAILURE_OUTCOME_ID;
    }
  }

  /**
   * Action that is fired when any form in the interaction is canceled.
   * @return outcome ID
   */
  public String cancel() {
    if (!conversation.isTransient()) {
      conversation.end();
    }
    return CANCEL_OUTCOME_ID;
  }

}
