/*
 * File created on Mar 3, 2014 
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

/**
 * A bean that supports the Dashboard View.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class DashboardViewBean {

  @Inject
  protected FacesContext facesContext;
  
  public void redirect() {
    ExternalContext externalContext = facesContext.getExternalContext();
    String contextPath = externalContext.getRequestContextPath();
    try {
      externalContext.redirect(contextPath + "/credentials/index.xhtml");
      facesContext.responseComplete();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
