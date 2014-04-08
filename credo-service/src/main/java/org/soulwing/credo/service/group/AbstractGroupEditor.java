/*
 * File created on Mar 14, 2014 
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
package org.soulwing.credo.service.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.repository.UserGroupRepository;
import org.soulwing.credo.repository.UserProfileRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.crypto.WrappedWith;
import org.soulwing.credo.service.protect.GroupProtectionService;

/**
 * An abstract base for {@link ConfigurableGroupEditor} implementations
 * 
 * @author Carl Harris
 */
abstract class AbstractGroupEditor implements ConfigurableGroupEditor,
    Serializable {

  private static final long serialVersionUID = 1017537361170816221L;
  
  private UserGroup group;
  private Long userId;
  private String owner;
  private Collection<UserDetail> users;
  private Password password;
  private List<Long> membership = new ArrayList<>();

  @Inject
  protected GroupProtectionService protectionService;

  @Inject @WrappedWith(WrappedWith.Type.AES)
  protected SecretKeyEncryptionService secretKeyEncryptionService;
  
  @Inject
  protected UserProfileRepository profileRepository;

  @Inject
  protected UserGroupRepository groupRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public void setGroup(UserGroup group) {
    this.group = group;
    if (group != null) {
      UserGroup owner = group.getOwner();
      if (owner != null) {
        this.owner = owner.getName();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setUserId(Long id) {
    this.userId = id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setUsers(Collection<UserDetail> users) {
    this.users = users;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return group.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return group.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    group.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOwner() {
    if (owner == null) return UserGroup.SELF_GROUP_NAME;
    return owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(String owner) {
    if (owner != null) {
      owner = owner.trim();
      if (owner.equalsIgnoreCase(UserGroup.SELF_GROUP_NAME)) {
        owner = null;
      }
    }
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return group.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(String description) {
    group.setDescription(description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long[] getMembership() {
    return membership.toArray(new Long[membership.size()]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMembership(Long[] membership) {
    this.membership.clear();
    this.membership.addAll(Arrays.asList(membership));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<UserDetail> getAvailableUsers() {
    return users;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Password getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassword(Password password) {
    this.password = password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(Errors errors) throws GroupEditException,
      NoSuchGroupException, PassphraseException, GroupAccessException,
      MergeConflictException {
    
    Validate.isTrue(!UserGroup.SELF_GROUP_NAME.equals(getName()),
        "group name cannot be '" + UserGroup.SELF_GROUP_NAME + "'");

    try {
      beforeSave(errors);
      group = saveGroup(group, errors);
      UserGroup ownerGroup = resolveOwner(errors);
  
      if (ownerGroup == null && 
          !membership.contains(userId)) {
        membership.add(userId);
        errors.addWarning("members", "groupEditorUserMustBeMember");
      }
      
      SecretKeyWrapper secretKey = createSecretKey(group, errors);
      if (ownerGroup != null) {
        setOwnerSecretKey(secretKey, ownerGroup);
      }
      addNewMembers(secretKey, errors);
      if (errors.hasWarnings() || errors.hasErrors()) {
        throw new GroupEditException();
      }
      afterSave(errors);
    }
    catch (GroupAccessException ex) {
      errors.addError("groupAccessDenied", new Object[] { ex.getGroupName() });
      throw ex;
    }
  }

  private void setOwnerSecretKey(SecretKeyWrapper secretKey,
      UserGroup ownerGroup) throws PassphraseException, GroupAccessException {
    try {
      SecretKeyWrapper ownerKey = protectionService.unprotect(
          ownerGroup, password);
      SecretKeyWrapper groupOwnerKey = secretKeyEncryptionService.encrypt(
          secretKey, ownerKey.derive());
      group.setSecretKey(groupOwnerKey.getContent());
    }
    catch (UserAccessException ex) {
      throw new PassphraseException();
    }
  }

  private void addNewMembers(SecretKeyWrapper secretKey, Errors errors) {
    for (Long userId : membership) {
      if (isNewMember(userId)) {
        UserProfile profile = profileRepository.findById(userId);
        if (profile != null) {
          protectionService.protect(group, secretKey, profile);
        }
        else {
          UserDetail user = findUserDetail(userId);
          errors.addWarning("members", "groupEditorNoSuchUser",
              user.getLoginName(), user.getFullName());
        }
      }
    }
  }

  private UserGroup resolveOwner(Errors errors) throws GroupEditException {
    UserGroup ownerGroup = null;
    if (owner != null) {
      ownerGroup = groupRepository.findByGroupName(owner, null);
      group.setOwner(ownerGroup);
      if (ownerGroup == null) {
        errors.addError("owner", "groupNotFound", owner);
        throw new GroupEditException();
      }
    }
    return ownerGroup;
  }
  
  protected abstract SecretKeyWrapper createSecretKey(UserGroup group, Errors errors)
      throws PassphraseException, GroupAccessException;

  protected abstract boolean isNewMember(Long userId);
  
  protected void beforeSave(Errors errors) {    
  }
  
  protected abstract UserGroup saveGroup(UserGroup group, Errors errors)
      throws MergeConflictException, GroupAccessException;
  
  protected void afterSave(Errors errors) {    
  }
  
  private UserDetail findUserDetail(Long id) {
    for (UserDetail user : users) {
      if (user.getId().equals(id)) {
        return user;
      }
    }
    throw new IllegalStateException("cannot find user detail with ID " + id);
  }

}
