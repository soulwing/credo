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

import java.security.AlgorithmParameterGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.SecretKeyEncryptionService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.crypto.WrappedWith;
import org.soulwing.credo.service.crypto.WrappedWith.Type;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A service that encrypts (wraps) secret (symmetric) keys using an RSA
 * public key
 *
 * @author Carl Harris
 */
@WrappedWith(Type.AES)
@ApplicationScoped
public class JcaAESSecretKeyEncryptionService
    implements SecretKeyEncryptionService {

  private static final String TRANSFORM = "AES/CBC/PKCS5Padding";
  
  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  private AlgorithmParameterGenerator parameterGenerator;

  /**
   * Initializes the receiver.
   */
  @PostConstruct
  public void init() {
    try {
      parameterGenerator = AlgorithmParameterGenerator.getInstance(
          TRANSFORM.substring(0, TRANSFORM.indexOf("/")));
      parameterGenerator.init(256);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper encrypt(SecretKeyWrapper secretKey,
      Key key) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORM);
      cipher.init(Cipher.WRAP_MODE, key, 
          parameterGenerator.generateParameters());
      
      byte[] iv = cipher.getParameters()
          .getParameterSpec(IvParameterSpec.class).getIV();        

      byte[] cipherText = cipher.wrap(secretKey.derive());
      return new JcaAESEncryptedSecretKeyWrapper(TRANSFORM, iv, cipherText, 
          objectBuilderFactory);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchPaddingException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidParameterSpecException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidAlgorithmParameterException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalBlockSizeException ex) {
      throw new RuntimeException(ex);
    }

  }

}
