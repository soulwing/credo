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
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

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

  private final PartContent file0 = new PartContent();
  private final PartContent file1 = new PartContent();
  private final PartContent file2 = new PartContent();

  @Inject
  protected Conversation conversation;

  @Inject
  protected Errors errors;

  @Inject
  protected ImportService importService;

  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected DelegatingCredentialEditor<ImportDetails> editor;

  @Inject
  protected PasswordFormEditor passwordEditor;

  private List<FileContentModel> files;
  private Password passphrase;
  private Credential credential;

  /**
   * Gets the {@code file0} property.
   * @return
   */
  public Part getFile0() {
    return file0.getPart();
  }

  /**
   * Sets the {@code file0} property.
   * @param file0
   */
  public void setFile0(Part part) {
    file0.setPart(part);
  }

  /**
   * Gets the {@code file1} property.
   * @return
   */
  public Part getFile1() {
    return file1.getPart();
  }

  /**
   * Sets the {@code file1} property.
   * @param file1
   */
  public void setFile1(Part part) {
    file1.setPart(part);
  }

  /**
   * Gets the {@code file2} property.
   * @return
   */
  public Part getFile2() {
    return file2.getPart();
  }

  /**
   * Sets the {@code file2} property.
   * @param file2
   */
  public void setFile2(Part part) {
    file2.setPart(part);
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
      editor.setDelegate(
          importService.prepareImport(fileList(), passphrase, errors));
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
      importService.saveCredential(credential, errors);
      conversation.end();
      return SUCCESS_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return null;
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

  /**
   * Produces a list containing the files that were uploaded by the user.
   * @return list of file content models
   * @throws IOException
   */
  private List<FileContentModel> fileList() throws IOException {
    if (files == null) {
      files = new ArrayList<FileContentModel>();
      if (file0.isLoadable()) {
        file0.load();
        files.add(file0);
      }
      if (file1.isLoadable()) {
        file1.load();
        files.add(file1);
      }
      if (file2.isLoadable()) {
        file2.load();
        files.add(file2);
      }
    }
    return files;
  }

}
