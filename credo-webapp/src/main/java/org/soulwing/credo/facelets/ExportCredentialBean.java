/*
 * File created on Feb 21, 2014 
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
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportFormat;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.ExportService;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;

/**
 * A bean that supports the Export Credential interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class ExportCredentialBean implements Serializable {

  private static final long serialVersionUID = 7577397736072518836L;

  static final String CANCEL_OUTCOME_ID = "cancel";
  static final String PREPARED_OUTCOME_ID = "prepared";
  static final String FAILURE_OUTCOME_ID = "failure";
  
  static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
 
  @Inject
  protected Conversation conversation;
  
  @Inject
  protected ExportService exportService;
  
  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected Errors errors;
  
  @Inject
  protected PasswordFormEditor passwordEditor;

  protected Long id;
    
  private ExportRequest request;
  
  private ExportPreparation preparation;

  private Password passphraseAgain;

  private ExportFormat selectedFormat;
  
  private ExportFormat.Variant selectedVariant;
  
  /**
   * Gets the {@code id} property.
   * @return
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the {@code id} property.
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the friendly name assigned to the credential.
   * @return friendly name
   */
  public String getName() {
    if (request == null) return null;
    return request.getCredential().getName();
  }
  
  /**
   * Gets the issuer name of the credential certificate.
   * @return issuer name
   */
  public String getIssuer() {
    if (request == null) return null;
    return request.getCredential().getIssuer();
  }
  
  /**
   * Gets the date at which the credential's certificate expires.
   * @return expiration date
   */
  public Date getExpiration() {
    if (request == null) return null;
    return request.getCredential().getExpiration();
  }
  
  /**
   * Gets the note assigned to the credential.
   * @return note
   */
  public String getNote() {
    if (request == null) return null;
    return request.getCredential().getNote();
  }
  
  /**
   * Gets the tags assigned to this credential.
   * @return tags
   */
  public String getTags() {
    if (request == null) return null;
    StringBuilder sb = new StringBuilder();
    Set<? extends Tag> tags = request.getCredential().getTags();
    int i = 0;
    int size = tags.size();
    for (Tag tag : tags) {
      sb.append(tag.getText());
      if (++i < size) {
        sb.append(", ");
      }     
    }
    return sb.toString();
  }

  /**
   * Gets the export file name.
   * @return file name
   */
  public String getFileName() {
    if (request == null) return null;
    return request.getFileName();
  }
  
  /**
   * Sets the export file name.
   * @param fileName the file name to set
   */
  public void setFileName(String fileName){
    Validate.notNull(request, "request not prepared");
    request.setFileName(fileName);
  }
  
  /**
   * Gets the supported export formats.
   * @return export formats
   */
  public Collection<ExportFormat> getSupportedFormats() {
    return exportService.getFormats();
  }

  /**
   * Gets the supported variants for the selected export format.
   * @return supported variants
   */
  public Collection<ExportFormat.Variant> getSupportedVariants() {
    return selectedFormat.getVariants();
  }
  
  /**
   * Gets the export format identifier.
   * @return format identifier or {@code null} if none has been set
   */
  public String getFormat() {
    if (request == null) return null;
    return request.getFormat();
  }

  /**
   * Sets the export format identifier.
   * @param format the format identifier to set
   */
  public void setFormat(String format) {
    Validate.notNull(request, "request not prepared");
    request.setFormat(format);
  }
  
  /**
   * Sets the selected format.
   * <p>
   * This method is exposed for unit testing.
   * @param selectedFormat the format to set
   */
  void setSelectedFormat(ExportFormat selectedFormat) {
    this.selectedFormat = selectedFormat;
  }
  
  /**
   * Gets the export format variant identifier.
   * @return variant identifier or {@code null} if none has been set
   */
  public String getVariant() {
    if (request == null) return null;
    return request.getVariant();    
  }
  
  /**
   * Sets the export format variant identifier.
   * @param variant the variant identifier to set
   */
  public void setVariant(String variant) {
    Validate.notNull(request, "request not prepared");
    request.setVariant(variant);
  }
  
  /**
   * A listener method that is invoked when a format is selected.
   * @param event subject event
   */
  public void formatSelected(AjaxBehaviorEvent event) {   
    selectedFormat = exportService.findFormat(getFormat());
    setVariant(selectedFormat.getDefaultVariant().getId());
    variantSelected(event);
  }

  /**
   * A listener method that is invoked when a variant is selected.
   * @param event subject event
   */
  public void variantSelected(AjaxBehaviorEvent event) {   
    selectedVariant = selectedFormat.findVariant(getVariant());
    setFileName(replaceSuffix(getFileName(), 
        selectedVariant.getSuffix()));
  }

  private String replaceSuffix(String fileName, String suffix) {
    int index = fileName.lastIndexOf('.');
    if (index == -1) {
      index = fileName.length();
    }
    return fileName.substring(0, index) + suffix;
  }

  /**
   * Tests whether the selected format requires a passphrase.
   * @return {@code true} if a passphrase is required
   */
  public boolean isPassphraseRequired() {
    if (selectedFormat == null) return false;
    return selectedFormat.isPassphraseRequired();
  }

  /**
   * Gets the export passphrase.
   * @return the export passphrase or {@code null} if none has been set
   */
  public Password getExportPassphrase() { 
    if (request == null) return null;
    return request.getExportPassphrase();
  }

  /**
   * Sets the export passphrase.
   * @param exportPassphase the passphrase to set
   */
  public void setExportPassphrase(Password exportPassphrase) {
    Validate.notNull(request, "request not prepared");
    request.setExportPassphrase(exportPassphrase);
  }

  /**
   * Gets the export passphrase validation property.
   * @return validation property value or {@code null} if none has been set
   */
  public Password getExportPassphraseAgain() { 
    if (request == null) return null;
    return passphraseAgain;
  }

  /**
   * Sets the export passphrase validation property.
   * @param passphraseAgain the validation property value to set
   */
  public void setExportPassphraseAgain(Password passphraseAgain) {
    Validate.notNull(request, "request not prepared");
    this.passphraseAgain = passphraseAgain;
  }

  /**
   * Generates a random export passphrase.
   * @param event source event
   */
  public void generateExportPassphrase(AjaxBehaviorEvent event) {
    FacesAjaxUtil.resetRenderedInputs(facesContext);
    generateExportPassphrase();
  }

  /**
   * Generates a random export passphrase.
   * <p>
   * This method is exposed to support unit testing.
   */
  void generateExportPassphrase() {
    Password passphrase = exportService.generatePassphrase();
    setExportPassphrase(passphrase);
    setExportPassphraseAgain(passphrase);
  }
  
  /**
   * Gets the editor that supports the password entry form.
   * @return editor
   */
  public PasswordFormEditor getPasswordEditor() {
    return passwordEditor;
  }
  
  /**
   * Gets the export request.
   * <p>
   * This method is exposed to support unit testing.
   * @return export request
   */
  ExportRequest getExportRequest() {
    return request;
  }

  /**
   * Sets the export request.
   * <p> 
   * This method is exposed to support unit testing.
   * @param request the export request to set
   */
  void setExportRequest(ExportRequest request) {
    this.request = request;
  }

  /**
   * Gets the export preparation.
   * <p>
   * This method is exposed to support unit testing.
   * @return export preparation
   */
  ExportPreparation getExportPreparation() {
    return preparation;
  }
  
  /**
   * Sets the export preparation.
   * <p>
   * This method is exposed to support unit testing.
   * @param preparation the preparation to set
   */
  void setExportPreparation(ExportPreparation preparation) {
    this.preparation = preparation;
  }
  
  /**
   * Gets the conversation associated with the receiver.
   * @return
   */
  public Conversation getConversation() {
    return conversation;
  }

  /**
   * Creates the export request.
   * @return outcome ID
   */
  public String createExportRequest() {
    if (id == null) {
      errors.addError("id", "credentialIdIsRequired");
      return null;
    }
    try {
      request = exportService.newExportRequest(id);
      passwordEditor.setGroupName(request.getCredential().getOwner().getName());
      request.setProtectionParameters(passwordEditor);
      setFormat(exportService.getDefaultFormat().getId());
      formatSelected(null);
      if (conversation.isTransient()) {
        conversation.begin();
      }
    }
    catch (NoSuchCredentialException ex) {
      errors.addError("credentialNotFound", id);
    }
    return null;
  }
  
  /**
   * Prepares the exported credential for download.
   * @return outcome ID
   */
  public String prepareDownload() {
    Validate.notNull(request, "request not created");
    try {
      preparation = exportService.prepareExport(request);
      return PREPARED_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      errors.addError("passphrase", "passphraseIncorrect");
      return null;
    }
    catch (GroupAccessException ex) {
      errors.addError("groupAccessDenied", new Object[] { ex.getGroupName() });
      return FAILURE_OUTCOME_ID;
    }
    catch (ExportException ex) {
      return FAILURE_OUTCOME_ID;
    }
  }

  /**
   * Downloads the prepared exported credential.
   */
  public void download() {
    Validate.notNull(preparation, "download not prepared");
    
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseContentType(preparation.getContentType());
    externalContext.setResponseHeader(CONTENT_DISPOSITION_HEADER,
        "attachment; filename=\"" + preparation.getFileName() + "\"");
    
    String encoding = preparation.getCharacterEncoding();
    if (encoding != null) {
      externalContext.setResponseCharacterEncoding(encoding);    
    }
    
    try {
      preparation.writeContent(externalContext.getResponseOutputStream());
      facesContext.responseComplete();
      conversation.setTimeout(60000);      
    }
    catch (IOException ex) {
      conversation.end();
      // FIXME
      throw new RuntimeException(ex);
    }
  }

  /**
   * Cancels the export/download.
   * @return outcome ID
   */
  public String cancel() {
    if (!conversation.isTransient()) {
      conversation.end();
    }
    return CANCEL_OUTCOME_ID;
  }
  
}
