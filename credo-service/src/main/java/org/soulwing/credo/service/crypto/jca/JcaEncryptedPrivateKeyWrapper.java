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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PrivateKeyWrapper} that delegates to a JCA {@link PrivateKey}.
 *
 * @author Carl Harris
 */
public class JcaEncryptedPrivateKeyWrapper implements PrivateKeyWrapper {

  private final String transform;
  private final byte[] iv;
  private final byte[] cipherText;  
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  private SecretKey secretKey;
  
  /**
   * Constructs a new instance.
   * @param transform the cryptographic transform that was applied to 
   *    encrypt the key
   * @param iv initialization vector
   * @param cipherText cipher text of secret key's DER encoding
   * @param objectBuilderFactory PEM object builder factory
   */
  public JcaEncryptedPrivateKeyWrapper(String transform, byte[] iv,
      byte[] cipherText, PemObjectBuilderFactory objectBuilderFactory) {
    this.transform = transform;
    this.iv = iv;
    this.cipherText = cipherText;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isProtected() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKey getProtectionParameter() {
    return secretKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProtectionParameter(Object parameter) {
    Validate.isTrue(parameter instanceof SecretKey,
        "requires a " + SecretKey.class.getSimpleName());
    this.secretKey = (SecretKey) parameter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    return objectBuilderFactory.newBuilder()
        .setType("RSA PRIVATE KEY")
        .setHeader("Proc-Type", "4,ENCRYPTED")
        .setHeader("DEK-Info", transform + "," + Hex.encodeHexString(iv))
        .append(cipherText)
        .build().getEncoded();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey derive() {
    try {
      Cipher cipher = Cipher.getInstance(transform);
      cipher.init(Cipher.UNWRAP_MODE, secretKey, new IvParameterSpec(iv));
      return (PrivateKey) cipher.unwrap(cipherText, "RSA", 
          Cipher.PRIVATE_KEY);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchPaddingException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidAlgorithmParameterException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper deriveWrapper() {
    return new JcaPrivateKeyWrapper(derive(), objectBuilderFactory);
  }

}
