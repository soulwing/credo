/*
 * File created on Mar 18, 2014 
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
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.Password;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.credential.CredentialEditor;
import org.soulwing.credo.service.group.GroupService;

/**
 * A {@link CredentialEditor} that delegates to another.
 *
 * @author Carl Harris
 */
@Dependent
public class DelegatingCredentialEditor<T extends CredentialEditor> 
    implements Serializable, CredentialEditor {

  private static final long serialVersionUID = 123021515868469526L;

  public enum OwnerStatus {
    NONE, EXISTS, WILL_CREATE, INACCESSIBLE
  }

  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected GroupService groupService;
  
  private T delegate;
  private OwnerStatus ownerStatus = OwnerStatus.EXISTS;
  
  /**
   * Gets the editor delegate.
   * @return delegate
   */
  public T getDelegate() {
    return delegate;
  }

  /**
   * Sets the editor delegate.
   * @param delegate the delegate to set.
   */
  public void setDelegate(T delegate) {
    this.delegate = delegate;
  }

  @Override
  public X500Principal getSubject() {
    return delegate.getSubject();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubjectCommonName() {
    return delegate.getSubjectCommonName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuerCommonName() {
    return delegate.getIssuerCommonName();
  }

  @Override
  public Date getExpiration() {
    return delegate.getExpiration();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return delegate.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    delegate.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOwner() {
    return delegate.getOwner();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(String owner) {
    delegate.setOwner(owner);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getNote() {
    return delegate.getNote();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNote(String note) {
    delegate.setNote(note);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getTags() {
    return delegate.getTags();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTags(String[] tags) {
    delegate.setTags(tags);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Password getPassword() {
    return delegate.getPassword();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassword(Password password) {
    delegate.setPassword(password);
  }

  /**
   * Gets the status of the group specified as the credential's owner.
   * @return status
   */
  public OwnerStatus getOwnerStatus() {
    return ownerStatus;
  }

  /**
   * Sets the status of the group specified as the credential's owner.
   * @param ownerStatus
   */
  public void setOwnerStatus(OwnerStatus ownerStatus) {
  }

  /**
   * Event handler that handles a change in the owner property.
   * @param event
   */
  public void ownerChanged(ValueChangeEvent event) {
//    FacesAjaxUtil.resetRenderedInputs(facesContext);
    try {
      String value = event.getNewValue().toString();
      if (value == null || value.isEmpty()) {
        ownerStatus = OwnerStatus.NONE;
      }
      else {
        ownerStatus = groupService.isExistingGroup(value) ? 
            OwnerStatus.EXISTS : OwnerStatus.WILL_CREATE;
      }
    }
    catch (GroupAccessException ex) {
      ownerStatus = OwnerStatus.INACCESSIBLE;
    }
  }

}
