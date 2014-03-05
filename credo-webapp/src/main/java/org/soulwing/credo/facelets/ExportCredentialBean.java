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
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Tag;
import org.soulwing.credo.service.AccessDeniedException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ExportException;
import org.soulwing.credo.service.ExportPreparation;
import org.soulwing.credo.service.ExportRequest;
import org.soulwing.credo.service.ExportService;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;

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

  private final ProtectionParametersBean protection = 
      new ProtectionParametersBean();
 
  @Inject
  protected Conversation conversation;
  
  @Inject
  protected ExportService exportService;
  
  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected Errors errors;
  
  protected Long id;
  
  private ExportRequest request;
  
  private ExportPreparation preparation;

  /**
   * Initializes the receiver.
   */
  @PostConstruct
  public void init() {
    protection.setLoginName(
        facesContext.getExternalContext().getRemoteUser());
  }
  
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
   * Gets the protection parameters.
   * @return protection parameters
   */
  public ProtectionParameters getProtection() {
    return protection;
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
    Validate.notNull(id, "id is required");
    try {
      request = exportService.newExportRequest(id);
      protection.setGroupName(request.getCredential().getOwner().getName());
      request.setProtectionParameters(protection);
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
    catch (AccessDeniedException ex) {
      // FIXME -- should divert to an error view for this
      throw new RuntimeException(ex);
    }
    catch (PassphraseException ex) {
      errors.addError("passphrase", "passphraseIncorrect");
      return null;
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
