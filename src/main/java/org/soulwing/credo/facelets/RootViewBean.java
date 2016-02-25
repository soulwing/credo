/*
 * File created on Mar 2, 2014 
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

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.WelcomeService;

/**
 * A bean that handles requests for the root view of the application.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class RootViewBean {

  static final String NEW_USER_PATH = "/welcome/index.xhtml";
  
  static final String EXISTING_USER_PATH = "/dashboard/index.xhtml";
  
  @Inject
  protected WelcomeService welcomeService;
  
  @Inject
  protected FacesContext facesContext;
  
  /**
   * Determines where to redirect a user who arrives at the root view.
   */
  public void redirect() {
    try {
      StringBuilder sb = new StringBuilder();
      ExternalContext externalContext = facesContext.getExternalContext();
      sb.append(externalContext.getRequestContextPath());
      sb.append(welcomeService.isNewUser() ? 
          NEW_USER_PATH : EXISTING_USER_PATH);
      externalContext.redirect(sb.toString());
      facesContext.responseComplete();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
