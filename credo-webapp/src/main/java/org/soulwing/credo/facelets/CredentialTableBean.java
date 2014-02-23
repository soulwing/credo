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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.Credential;
import org.soulwing.credo.service.CredentialService;

/**
 * A bean that supports a view containing a table of credentials.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class CredentialTableBean {

  @Inject
  protected CredentialService credentialService;
  
  private List<CredentialBean> beans;
  
  /**
   * Gets the collection of credentials to display in the table.
   * @return credential list
   */
  public List<CredentialBean> getCredentials() {
    if (beans == null) {
      List<Credential> credentials = credentialService.findAllCredentials();
      beans = new ArrayList<>(credentials.size());
      for (Credential credential : credentials) {
        beans.add(new CredentialBean(credential));
      }
    }
    return beans;
  }
  
}
