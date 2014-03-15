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
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.UserDetail;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.protect.GroupAccessException;
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
  private Long ownerId;
  private Collection<UserDetail> users;
  private Password password;
  private List<Long> membership = new ArrayList<>();

  @Inject
  protected GroupProtectionService protectionService;

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
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(Long id) {
    this.ownerId = id;
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
      NoSuchGroupException, PassphraseException, GroupAccessException {
    
    Validate.isTrue(!UserGroup.SELF_GROUP_NAME.equals(getName()),
        "group name cannot be '" + UserGroup.SELF_GROUP_NAME + "'");

    beforeSave(errors);
    group = saveGroup(group);

    if (!membership.contains(ownerId)) {
      membership.add(ownerId);
      errors.addWarning("members", "groupEditorUserMustBeMember");
    }
    
    SecretKeyWrapper secretKey = createSecretKey(group, errors);
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
    if (errors.hasWarnings() || errors.hasErrors()) {
      throw new GroupEditException();
    }
    afterSave(errors);
  }
  
  protected abstract SecretKeyWrapper createSecretKey(UserGroup group, Errors errors)
      throws PassphraseException, GroupAccessException;

  protected abstract boolean isNewMember(Long userId);
  
  protected void beforeSave(Errors errors) {    
  }
  
  protected abstract UserGroup saveGroup(UserGroup group);
  
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