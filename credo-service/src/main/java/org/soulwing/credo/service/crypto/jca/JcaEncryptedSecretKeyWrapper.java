/*
 * File created on Mar 27, 2014 
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
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * An abstract base for an encrypted {@link SecretKeyWrapper}.
 * 
 * @author Carl Harris
 */
public abstract class JcaEncryptedSecretKeyWrapper
    implements SecretKeyWrapper {

  protected final String transform;
  protected final byte[] cipherText;
  protected final PemObjectBuilderFactory objectBuilderFactory;
  private Key key;

  /**
   * Constructs a new instance.
   * @param transform the cryptographic transform that was applied to 
   *    encrypt the key
   * @param cipherText cipher text of secret key's DER encoding
   * @param objectBuilderFactory PEM object builder factory
   */
  protected JcaEncryptedSecretKeyWrapper(String transform,
      byte[] cipherText, PemObjectBuilderFactory objectBuilderFactory) {
    this.transform = transform;
    this.cipherText = cipherText;
    this.objectBuilderFactory = objectBuilderFactory;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEncrypted() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Key getKey() {
    return key;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setKey(Key key) {
    this.key = key;    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    return objectBuilderFactory.newBuilder()
        .setType("ENCRYPTED SECRET KEY")
        .setHeader("Proc-Type", "4,ENCRYPTED")
        .setHeader("DEK-Info", getDEKInfo())
        .append(cipherText)
        .build().getEncoded();
  }

  /**
   * Gets the value for the DEK-Info header.
   * @return header value
   */
  protected String getDEKInfo() {
    return transform;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKey derive() {
    try {
      return (SecretKey) createCipher().unwrap(cipherText, "AES", 
          Cipher.SECRET_KEY);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchPaddingException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidParameterSpecException ex) {
      throw new RuntimeException(ex);    
    }
    catch (InvalidAlgorithmParameterException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Creates the cipher needed to decrypt the secret key.
   * @return cipher
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   * @throws InvalidKeyException
   * @throws InvalidParameterSpecException
   * @throws InvalidAlgorithmParameterException
   */
  protected abstract Cipher createCipher() 
      throws NoSuchAlgorithmException, NoSuchPaddingException, 
      InvalidKeyException, InvalidParameterSpecException,
      InvalidAlgorithmParameterException;

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper deriveWrapper() {
    return new JcaSecretKeyWrapper(derive(), objectBuilderFactory);
  }

}
