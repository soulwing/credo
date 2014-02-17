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

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.repository.TagRepository;

/**
 * A concrete implementation of {@link ImportService}.
 *
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteImportService implements ImportService {

  @Inject
  protected CredentialBuilderFactory credentialBuilderFactory;
  
  @Inject
  protected CredentialRepository credentialRepository;
  
  @Inject
  protected TagRepository tagRepository;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ImportPreparation prepareImport(List<FileContentModel> files,
      Errors errors) throws ImportException {
    if (files.isEmpty()) {
      errors.addError("importFileRequired");
      throw new ImportException();
    }
    CredentialBuilder builder = credentialBuilderFactory.newInstance();
    int i = 0;
    for (FileContentModel file : files) {
      try {
        builder.loadFile(file.getInputStream());
        
        i++;
      }
      catch (NoContentException ex) {
        errors.addError("file" + i, "importNoContent",
            file.getName());
      }
      catch (IOException ex) {
        errors.addError("file" + i, "importReadError",
            file.getName());
      }
    }
    if (errors.hasErrors()) {
      throw new ImportException();
    }
    
    return builder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential createCredential(ImportPreparation preparation,
      Errors errors) throws ImportException {
    if (!(preparation instanceof CredentialBuilder)) {
      throw new IllegalArgumentException(
          "preparation was not created by this service");
    }

    CredentialBuilder builder = (CredentialBuilder) preparation;
    builder.validate(errors);
    return builder.build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void saveCredential(Credential credential, Errors errors)
      throws ImportException {
    credentialRepository.add(credential);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<? extends Tag> resolveTags(String[] tokens) {
    Set<Tag> tags = new LinkedHashSet<>();
    for (String token : tokens) {
      Tag tag = tagRepository.findByTagText(token);
      if (tag == null) {
        tag = tagRepository.newInstance(token);
      }
      tags.add(tag);
    }
    return tags;
  }
  
}

