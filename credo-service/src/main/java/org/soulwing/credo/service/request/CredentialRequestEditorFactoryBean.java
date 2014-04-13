/*
 * File created on Mar 20, 2014 
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
package org.soulwing.credo.service.request;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.CredentialRequest;

/**
 * A concrete {@link CredentialRequestEditorFactory} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class CredentialRequestEditorFactoryBean
    implements CredentialRequestEditorFactory {

  @Inject
  protected Instance<ConfigurableRequestEditor> configurableEditors;
  
  @Inject
  protected Instance<DelegatingRequestEditor> delegatingEditors;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestEditor newEditor(Credential credential) {
    Validate.isTrue(!credential.getCertificates().isEmpty());
    CredentialCertificate certificate = credential.getCertificates().get(0);
    ConfigurableRequestEditor editor = configurableEditors.get();
    editor.setCredentialId(credential.getId());
    editor.setSubjectName(certificate.getSubject());
    editor.setName(credential.getName());
    editor.setOwner(credential.getOwner().getName());
    editor.setNote(credential.getNote());
    editor.setTags(credential.getTags());
    return editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialRequestEditor newEditor(CredentialRequest request) {
    DelegatingRequestEditor editor = delegatingEditors.get();
    editor.setDelegate(request);
    return editor;
  }
  
}
