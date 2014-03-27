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

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
@WrappedWith(Type.RSA)
@ApplicationScoped
public class JcaRSASecretKeyEncryptionService
    implements SecretKeyEncryptionService {

  private static final String TRANSFORM = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
  
  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper encrypt(SecretKeyWrapper secretKey,
      Key key) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORM);
      cipher.init(Cipher.WRAP_MODE, key);
      byte[] cipherText = cipher.wrap(secretKey.derive());
      return new JcaRSAEncryptedSecretKeyWrapper(TRANSFORM, cipherText, 
          objectBuilderFactory);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchPaddingException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalBlockSizeException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }

  }

}
