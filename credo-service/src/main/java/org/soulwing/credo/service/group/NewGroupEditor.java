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

  @Inject
  protected KeyGeneratorService keyGeneratorService;

  /**
   * {@inheritDoc}
   */
  @Override
  protected SecretKeyWrapper createSecretKey(UserGroup group) {
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
  protected UserGroup saveGroup(UserGroup group) {
    groupRepository.add(group);   
    return group;
  }

}
