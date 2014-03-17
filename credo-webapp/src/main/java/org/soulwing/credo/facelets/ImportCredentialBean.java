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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.AccessDeniedException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportPreparation;
import org.soulwing.credo.service.ImportService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserProfileService;

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

  private static final long serialVersionUID = -5565484780336702769L;

  private final PartContent file0 = new PartContent();
  private final PartContent file1 = new PartContent();
  private final PartContent file2 = new PartContent();

  private final PasswordFormBean passwordFormBean = new PasswordFormBean();

  @Inject
  protected Conversation conversation;

  @Inject
  protected Errors errors;

  @Inject
  protected ImportService importService;

  @Inject
  protected UserProfileService profileService;

  @Inject
  protected FacesContext facesContext;

  private ImportPreparation preparation;
  private Credential credential;
  private OwnerStatus ownerStatus = OwnerStatus.EXISTS;

  @PostConstruct
  public void init() {
    passwordFormBean.setGroupName(UserGroup.SELF_GROUP_NAME);
    passwordFormBean.setExpected(profileService.getLoggedInUserProfile()
        .getPassword());
  }

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
   * Gets the {@code name} property.
   * @return
   */
  public String getName() {
    return credential.getName();
  }

  /**
   * Sets the {@code name} property.
   * @param name
   */
  public void setName(String name) {
    credential.setName(name);
  }

  /**
   * Tests whether the user is a member of the "self" group, only.
   * @return {@code true} if the user has no group memberships other than the
   *         "self" group
   */
  public boolean isMemberOfSelfGroupOnly() {
    return importService.isMemberOfSelfGroupOnly();
  }

  /**
   * Gets the owner name for the credential.
   * @return owner name or {@code null} if none has been set
   */
  public String getOwner() {
    return passwordFormBean.getGroupName();
  }

  /**
   * Sets the owner name for the credential.
   * @param owner the owner name to set
   */
  public void setOwner(String owner) {
    passwordFormBean.setGroupName(owner);
  }

  /**
   * Gets the status of the group specified as the credential's owner.
   * @return status
   */
  public OwnerStatus getOwnerStatus() {
    return ownerStatus;
  }

  /**
   * Sets the status of the group specified as the credential's owner.
   * @param ownerStatus
   */
  public void setOwnerStatus(OwnerStatus ownerStatus) {
  }

  /**
   * Gets the {@code note} property.
   * @return
   */
  public String getNote() {
    return credential.getNote();
  }

  /**
   * Sets the {@code note} property.
   * @param note
   */
  public void setNote(String note) {
    credential.setNote(note);
  }

  /**
   * Gets the {@code tags} property.
   * @return
   */
  public String getTags() {
    Set<? extends Tag> tags = credential.getTags();
    if (tags == null || tags.isEmpty())
      return "";
    int i = 0;
    StringBuilder sb = new StringBuilder();
    for (Tag tag : tags) {
      sb.append(tag.getText());
      if (++i < tags.size()) {
        sb.append(',');
      }
    }
    return sb.toString();
  }

  /**
   * Sets the {@code tags} property.
   * @param tags
   */
  public void setTags(String tags) {
    if (StringUtils.isBlank(tags)) {
      Set<Tag> tagSet = Collections.emptySet();
      credential.setTags(tagSet);
      return;
    }
    String[] tokens = tags.split("\\s*,\\s*");
    credential.setTags(importService.resolveTags(tokens));
  }

  /**
   * Gets the private key passphrase.
   * @return passphrase
   */
  public Password getPassphrase() {
    Validate.notNull(preparation, "import not prepared");
    return preparation.getPassphrase();
  }

  /**
   * Sets the private key passphrase.
   * @param passphrase the passphrase to set
   */
  public void setPassphrase(Password passphrase) {
    Validate.notNull(preparation, "import not prepared");
    preparation.setPassphrase(passphrase);
  }

  /**
   * Gets the supporting bean for the password entry form.
   */
  public PasswordFormBean getPasswordFormBean() {
    return passwordFormBean;
  }

  /**
   * Gets the import preparation produced by the import service.
   * @return preparation
   */
  public ImportPreparation getPreparation() {
    return preparation;
  }

  /**
   * Sets the import preparation produced by the import service.
   * <p>
   * This method is exposed to support unit testing.
   * @param preparation the preparation to set
   */
  void setPreparation(ImportPreparation preparation) {
    this.preparation = preparation;
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
   * Event handler that handles a change in the owner property.
   * @param event
   */
  public void ownerChanged(ValueChangeEvent event) {
    resetRenderedInputs();
    try {
      String value = event.getNewValue().toString();
      if (value == null || value.isEmpty()) {
        ownerStatus = OwnerStatus.NONE;
      }
      else {
        ownerStatus = importService.isExistingGroup(value) ? 
            OwnerStatus.EXISTS : OwnerStatus.WILL_CREATE;
      }
    }
    catch (GroupAccessException ex) {
      ownerStatus = OwnerStatus.INACCESSIBLE;
    }
  }

  private void resetRenderedInputs() {
    PartialViewContext partialViewContext = 
        facesContext.getPartialViewContext();
    Collection<String> renderIds = partialViewContext.getRenderIds();
    UIViewRoot viewRoot = facesContext.getViewRoot();
    for (String renderId : renderIds) {
      UIComponent component = viewRoot.findComponent(renderId);
      if (component instanceof EditableValueHolder) {
        EditableValueHolder input = (EditableValueHolder) component;
        input.resetValue();
      }
    }
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
      preparation = importService.prepareImport(fileList(), errors);
      if (!preparation.isPassphraseRequired()) {
        return validate();
      }
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
   * Action that is fired after files have been upload and (if necessary) the
   * passphrase for the imported private key has been entered by the user.
   * @return outcome ID
   */
  public String validate() {
    if (preparation == null) {
      throw new IllegalStateException("import not prepared");
    }
    try {
      if (preparation.isPassphraseRequired()) {
        ImportPreparation previous = preparation;
        preparation = importService.prepareImport(fileList(), errors);
        preparation.setPassphrase(previous.getPassphrase());
      }
      credential = importService.createCredential(preparation, errors);
      credential.setName(preparation.getDetails().getSubject());
      return DETAILS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      errors.addError("passphrase", "passphraseIncorrect");
      return PASSPHRASE_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return FAILURE_OUTCOME_ID;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Action that is fired when the form containing the credential details is
   * submitted with the {@code protect} action.
   * @return outcome ID
   */
  public String protect() {
    try {
      importService.protectCredential(credential, preparation,
          passwordFormBean, errors);
      return CONFIRM_OUTCOME_ID;
    }
    catch (AccessDeniedException ex) {
      // FIXME -- should divert to an error view for this
      throw new RuntimeException(ex);
    }
    catch (NoSuchGroupException ex) {
      return DETAILS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      return null;
    }
  }

  /**
   * Action that is fired when the form containing credential details is
   * submitted with the save action.
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
   * Action that is fired when the form containing credential details is
   * submitted with the cancel action.
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
    List<FileContentModel> files = new ArrayList<FileContentModel>();
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
    return files;
  }

}
