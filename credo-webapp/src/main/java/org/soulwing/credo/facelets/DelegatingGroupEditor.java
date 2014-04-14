/*
 * File created on Mar 28, 2014 
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

import javax.enterprise.context.Dependent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.soulwing.credo.Password;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.group.GroupEditor;
import org.soulwing.credo.service.group.GroupService;

/**
 * A {@link GroupEditor} that delegates to another.
 * <p>
 * This class provides some additional event handling for the UI.
 *
 * @author Carl Harris
 */
@Dependent
public class DelegatingGroupEditor implements GroupEditor, Serializable {

  private static final long serialVersionUID = -1338539136608595816L;

  public enum OwnerStatus {
    NONE, EXISTS, NOT_FOUND, INACCESSIBLE;
  }
  
  @Inject
  protected GroupService groupService;
  
  private GroupEditor delegate;
  
  private OwnerStatus ownerStatus;
  
  /**
   * Gets the delegate editor.
   * @return delegate
   */
  public GroupEditor getDelegate() {
    return delegate;
  }

  /**
   * Sets the delegate editor
   * @param delegate the editor to set
   */
  public void setDelegate(GroupEditor delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return delegate.getId();
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
  public String getDescription() {
    return delegate.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(String description) {
    delegate.setDescription(description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long[] getMembership() {
    return delegate.getMembership();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMembership(Long[] membership) {
    delegate.setMembership(membership);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<UserDetail> getAvailableUsers() {
    return delegate.getAvailableUsers();
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
    try {
      String value = event.getNewValue().toString();
      if (value == null || value.isEmpty()) {
        ownerStatus = OwnerStatus.NONE;
      }
      else {
        ownerStatus = groupService.isExistingGroup(value) ? 
            OwnerStatus.EXISTS : OwnerStatus.NOT_FOUND;
      }
    }
    catch (GroupAccessException ex) {
      ownerStatus = OwnerStatus.INACCESSIBLE;
    }
  }

}
