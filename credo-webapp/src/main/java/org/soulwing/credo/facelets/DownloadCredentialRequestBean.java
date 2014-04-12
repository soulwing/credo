/*
 * File created on Mar 17, 2014 
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

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.request.DownloadRequestService;

/**
 * A bean that supports the Download Credential Request interaction.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class DownloadCredentialRequestBean implements Serializable {

  private static final long serialVersionUID = 8209629154531518443L;

  static final String SUCCESS_OUTCOME_ID = "success";
  
  @Inject
  protected DownloadRequestService requestService;
   
  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected Errors errors;
  
  private Long id;
  
  /**
   * Gets the unique identifier for the request to be removed.
   * @return unique identifier
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier for the request to be removed.
   * @param id the unique identifier to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Downloads the specified credential.
   */
  public String download() {
    if (id != null) {
      try {
        requestService.downloadRequest(id, 
            new FacesFileDownloadResponse(facesContext));
        facesContext.responseComplete();
      }
      catch (NoSuchCredentialException ex) {
        assert true;  // ignore it        
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    return SUCCESS_OUTCOME_ID;
  }
  
}
