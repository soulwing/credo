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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportPreparation;
import org.soulwing.credo.service.ImportService;

/**
 * A bean that supports the Add Credential interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class AddCredentialBean implements Serializable {

  static final String DETAILS_OUTCOME_ID = "details";

  static final String WARNINGS_OUTCOME_ID = "warnings";
  
  static final String FAILURE_OUTCOME_ID = "failure";

  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  static final String PASSPHRASE_OUTCOME_ID = "passphrase";
  
  private static final long serialVersionUID = -5565484780336702769L;
  
  @Inject
  protected Conversation conversation;
  
  @Inject
  protected Errors errors;
  
  @Inject
  protected ImportService importService;
  
  private Part file0;
  private Part file1;
  private Part file2;
  
  private ImportPreparation preparation;

  private Credential credential;
  
  /**
   * Gets the {@code file0} property.
   * @return
   */
  public Part getFile0() {
    return file0;
  }

  /**
   * Sets the {@code file0} property.
   * @param file0
   */
  public void setFile0(Part file0) {
    this.file0 = file0;
  }

  /**
   * Gets the {@code file1} property.
   * @return
   */
  public Part getFile1() {
    return file1;
  }

  /**
   * Sets the {@code file1} property.
   * @param file1
   */
  public void setFile1(Part file1) {
    this.file1 = file1;
  }

  /**
   * Gets the {@code file2} property.
   * @return
   */
  public Part getFile2() {
    return file2;
  }

  /**
   * Sets the {@code file2} property.
   * @param file2
   */
  public void setFile2(Part file2) {
    this.file2 = file2;
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
   * Gets the {@code description} property.
   * @return
   */
  public String getDescription() {
    return credential.getDescription();
  }

  /**
   * Sets the {@code description} property.
   * @param description
   */
  public void setDescription(String description) {
    credential.setDescription(description);
  }

  /**
   * Gets the {@code tags} property.
   * @return
   */
  public String getTags() {
    Set<? extends Tag> tags = credential.getTags();
    if (tags == null || tags.isEmpty()) return "";
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
   * Gets the import preparation produced by the import service.
   * <p>
   * This method is exposed principally to support unit testing.
   * @return preparation
   */
  ImportPreparation getPreparation() {
    return preparation;
  }

  /**
   * Sets the import preparation produced by the import service.
   * <p>
   * This method is exposed principally to support unit testing.
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
   * Action that is fired when the form containing the files to import
   * has been submitted. 
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
  }
  
  public String validate() {
    if (preparation == null) {
      throw new IllegalStateException("import not prepared");
    }
    try {
      importService.createCredential(preparation, errors);
      return errors.hasWarnings() ? WARNINGS_OUTCOME_ID : DETAILS_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return FAILURE_OUTCOME_ID;
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
   */
  private List<FileContentModel> fileList() {
    List<FileContentModel> files = new ArrayList<FileContentModel>();
    if (getFile0() != null) {
      files.add(new PartContent(getFile0()));
    }
    if (getFile1() != null) {
      files.add(new PartContent(getFile1()));
    }
    if (getFile2() != null) {
      files.add(new PartContent(getFile2()));
    }
    return files;
  }
  
}
