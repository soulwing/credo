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
import java.util.Collection;
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.service.CredentialEditor;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.GroupService;

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
    resetRenderedInputs();
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

  /**
   * Resets rendered inputs on an Ajax request.  
   * <p>
   * This works around an issue when the form is submitted with one or more 
   * validation errors, allowing the rendered inputs to be properly updated on 
   * subsequent Ajax requests.
   */
  private void resetRenderedInputs() {
    PartialViewContext partialViewContext = 
        facesContext.getPartialViewContext();
    Collection<String> renderIds = partialViewContext.getRenderIds();
    UIViewRoot viewRoot = facesContext.getViewRoot();
    for (String renderId : renderIds) {
      UIComponent component = viewRoot.findComponent(renderId);
      if (component instanceof EditableValueHolder) {
        EditableValueHolder input = (EditableValueHolder) component;
        input.resetValue();
      }
    }
  }

}
