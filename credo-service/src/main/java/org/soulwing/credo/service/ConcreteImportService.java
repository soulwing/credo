/*
 * File created on Feb 14, 2014 
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
package org.soulwing.credo.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.domain.CredentialEntity;

/**
 * A concrete implementation of {@link ImportService}.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteImportService implements ImportService {

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential importCredential(List<FileContentModel> files,
      Errors errors) throws ImportException {
    return new CredentialEntity();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveCredential(Credential credential, Errors errors)
      throws ImportException {
    // TODO Auto-generated method stub
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<? extends Tag> resolveTags(String[] tokens) {
    return Collections.emptySet();
  }
  
}

