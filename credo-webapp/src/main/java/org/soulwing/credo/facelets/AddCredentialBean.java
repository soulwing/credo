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
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.ImportException;
import org.soulwing.credo.service.ImportService;

/**
 * A bean that supports the Add Credential interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class AddCredentialBean implements Serializable {

  public static final String FILE_REQUIRED_MESSAGE = "requiresAtLeastOneFile";

  public static final String DETAILS_OUTCOME_ID = "details";

  public static final String WARNINGS_OUTCOME_ID = "warnings";
  
  private static final long serialVersionUID = -5565484780336702769L;
  
  @Inject
  protected Errors errors;
  
  @Inject
  protected ImportService importService;
  
  private Part file0;
  private Part file1;
  private Part file2;
  
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
   * Action that is fired when the form containing the files to import
   * has been submitted. 
   * @return outcome ID
   */
  public String upload() {
    if (getFile0() == null && getFile1() == null && getFile2() == null) {
      errors.addError(FILE_REQUIRED_MESSAGE);
      return null;
    }
    try {
      importService.importCredential(fileList(), errors);
      return errors.hasWarnings() ? WARNINGS_OUTCOME_ID : DETAILS_OUTCOME_ID;
    }
    catch (ImportException ex) {
      return null;
    }
  }
  
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
