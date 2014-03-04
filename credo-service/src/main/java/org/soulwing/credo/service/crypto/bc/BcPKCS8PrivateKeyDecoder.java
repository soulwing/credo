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
package org.soulwing.credo.service.crypto.bc;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PrivateKeyDecoder} that decodes a PEM-encoded PKCS8 encrypted
 * private key.
 *
 * @author Carl Harris
 */
@Encoded(type = Encoded.Type.PKCS8)
@ApplicationScoped
public class BcPKCS8PrivateKeyDecoder implements PrivateKeyDecoder {

  @Inject
  private PemObjectFactory objectFactory;
  
  @Inject
  private PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper decode(String encoded) {
    PemObjectWrapper object = objectFactory.newPemObject(encoded);
    try {
      return new BcPrivateKeyWrapper(
          new PKCS8EncryptedPrivateKeyInfo(object.getContent()),
          objectBuilderFactory);
    }
    catch (IOException ex) {
      throw new IllegalArgumentException("invalid PKCS8 private key", ex);
    }
  }

}
