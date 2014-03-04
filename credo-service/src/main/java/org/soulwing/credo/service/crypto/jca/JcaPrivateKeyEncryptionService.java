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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.PrivateKeyEncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PrivateKeyEncryptionService} that wraps private keys using an
 * AES secret key via the JCA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaPrivateKeyEncryptionService
    implements PrivateKeyEncryptionService {

  private static final String TRANSFORM = "AES/CBC/PKCS5Padding";

  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper encrypt(PrivateKeyWrapper privateKey,
      SecretKeyWrapper secretKey) {
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORM);
      cipher.init(Cipher.WRAP_MODE, secretKey.derive());

      byte[] iv = cipher.getParameters()
          .getParameterSpec(IvParameterSpec.class).getIV();
          
      byte[] cipherText = cipher.wrap(privateKey.derive());
      return new JcaEncryptedPrivateKeyWrapper(TRANSFORM, iv, cipherText, 
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
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalBlockSizeException ex) {
      throw new RuntimeException(ex);
    }
  }

}
