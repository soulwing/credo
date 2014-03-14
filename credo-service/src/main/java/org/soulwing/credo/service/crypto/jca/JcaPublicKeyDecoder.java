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

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.PublicKeyDecoder;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PublicKeyDecoder} that is based on the JCA public key.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaPublicKeyDecoder implements PublicKeyDecoder {

  @Inject
  protected PemObjectFactory objectFactory;
  
  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  private KeyFactory keyFactory;
  
  @PostConstruct
  public void init() {
    try {
      keyFactory = KeyFactory.getInstance("RSA");
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PublicKeyWrapper decode(String encoded) {
    PemObjectWrapper object = objectFactory.newPemObject(encoded);
    KeySpec keySpec = new X509EncodedKeySpec(object.getContent());
    try {
      PublicKey publicKey = keyFactory.generatePublic(keySpec);
      return new JcaPublicKeyWrapper(publicKey, objectBuilderFactory);
    }
    catch (InvalidKeySpecException ex) {
      throw new RuntimeException(ex);
    }
  }

}
