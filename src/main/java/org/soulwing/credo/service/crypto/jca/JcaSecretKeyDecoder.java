/*
 * File created on Mar 3, 2014 
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.SecretKeyDecoder;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link SecretKeyDecoder} that decodes an PEM-encoded secret key.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaSecretKeyDecoder implements SecretKeyDecoder {

  @Inject
  protected PemObjectFactory objectFactory;
  
  @Inject
  protected Instance<JcaSecretKeyDecoderStrategy> strategies;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper decode(String encoded) {
    PemObjectWrapper object = objectFactory.newPemObject(encoded);
    for (JcaSecretKeyDecoderStrategy strategy : strategies) {
      SecretKeyWrapper secretKey = strategy.decode(object);
      if (secretKey != null) {
        return secretKey;
      }
    }
    throw new IllegalArgumentException("cannot decode the given object");
  }

}
