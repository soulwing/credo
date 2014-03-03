/*
 * File created on Mar 2, 2014 
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
package org.soulwing.credo.service.crypto.jca;

import java.security.KeyPair;

import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link KeyPairWrapper} that delegates to a JCA {@link KeyPair}.
 *
 * @author Carl Harris
 */
public class JcaKeyPairWrapper implements KeyPairWrapper {

  private final KeyPair keyPair;
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * Constructs a new instance.
   * @param keyPair
   */
  public JcaKeyPairWrapper(KeyPair keyPair, 
      PemObjectBuilderFactory objectBuilderFactory) {
    this.keyPair = keyPair;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PublicKeyWrapper getPublic() {
    return new JcaPublicKeyWrapper(keyPair.getPublic(),
        objectBuilderFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper getPrivate() {
    return new JcaPrivateKeyWrapper(keyPair.getPrivate(),
        objectBuilderFactory);
  }

}
