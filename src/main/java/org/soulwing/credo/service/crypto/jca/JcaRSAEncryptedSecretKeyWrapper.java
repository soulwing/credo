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

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link SecretKeyWrapper} that delegates to a JCA {@link SecretKey}.
 *
 * @author Carl Harris
 */
public class JcaRSAEncryptedSecretKeyWrapper 
    extends JcaEncryptedSecretKeyWrapper {

  /**
   * Constructs a new instance.
   * @param transform the cryptographic transform that was applied to 
   *    encrypt the key
   * @param cipherText cipher text of secret key's DER encoding
   * @param objectBuilderFactory PEM object builder factory
   */
  public JcaRSAEncryptedSecretKeyWrapper(String transform,
      byte[] cipherText, PemObjectBuilderFactory objectBuilderFactory) {
    super(transform, cipherText, objectBuilderFactory);
  }

  @Override
  protected Cipher createCipher() throws NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException {
    Cipher cipher = Cipher.getInstance(transform);
    cipher.init(Cipher.UNWRAP_MODE, getKey());
    return cipher;
  }

}
