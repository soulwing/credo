/*
 * File created on Mar 21, 2014 
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
package org.soulwing.credo.service.protect;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A concrete {@link CredentialRequestProtectionService} implementation.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteCredentialRequestProtectionService
    extends AbstractCredentialKeyProtectionService
    implements CredentialRequestProtectionService {

  /**
   * {@inheritDoc}
   */
  @Override
  public void protect(CredentialRequest request,
      PrivateKeyWrapper privateKey, ProtectionParameters protection)
      throws GroupAccessException, UserAccessException, NoSuchGroupException {

    UserGroup group = findGroup(protection.getGroupName());
    SecretKey secretKey = getGroupSecretKey(group, protection.getPassword());
    request.setOwner(group);
    request.getPrivateKey().setContent(
        wrapPrivateKey(privateKey, secretKey).getContent());
  }

}
