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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * A saveable editor for a new group.
 *
 * @author Carl Harris
 */
@NewGroup
@Dependent
public class NewGroupEditor extends AbstractGroupEditor {

  private static final long serialVersionUID = 8597883480608481992L;

  @Inject
  protected KeyGeneratorService keyGeneratorService;

  private String name;
  private String description;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void setGroup(UserGroup group) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SecretKeyWrapper createSecretKey(UserGroup group, Errors errors) {
    return keyGeneratorService.generateSecretKey();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isNewMember(Long userId) {
    return true;    // every member is new for a new group
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UserGroup saveGroup(Errors errors) {
    UserGroup group = groupRepository.newGroup(name);
    group.setDescription(description);
    groupRepository.add(group);   
    return group;
  }

}
