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
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link KeyGeneratorService} that is based on the JCA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaKeyGeneratorService implements KeyGeneratorService {

  private static final int PASSPHRASE_LENGTH = 64;
  private static final int SALT_LENGTH = 16;
  private static final int ITERATION_COUNT = 65536;
  private static final int KEY_LENGTH = 256;

  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  private SecureRandom secureRandom;
  private KeyPairGenerator keyPairGenerator;
  private SecretKeyFactory secretKeyFactory;
  
  @PostConstruct
  public void init() {
    try {
      secureRandom = SecureRandom.getInstance("SHA1PRNG");
      keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public KeyPairWrapper generateKeyPair() {
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    return new JcaKeyPairWrapper(keyPair, objectBuilderFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper generateSecretKey() {
    char[] passphrase = randomPassphrase(PASSPHRASE_LENGTH);
    byte[] salt = randomSalt(SALT_LENGTH);
    PBEKeySpec keySpec = new PBEKeySpec(passphrase, salt, ITERATION_COUNT, 
        KEY_LENGTH);
    try {
      SecretKey intermediate = secretKeyFactory.generateSecret(keySpec);
      return new JcaSecretKeyWrapper(
          new SecretKeySpec(intermediate.getEncoded(), "AES"),
          objectBuilderFactory);
    }
    catch (InvalidKeySpecException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Generates a random passphrase of the specified length.
   * @param length length of the passphrase to generate
   * @return passphrase
   */
  private char[] randomPassphrase(int length) {
    char[] passphrase = new char[length];
    for (int i = 0; i < length; i++) {
      char c = (char) secureRandom.nextInt(65536);
      while (!Character.isDefined(c)) {
        c = (char) secureRandom.nextInt(65536);
      }
      passphrase[i] = c;
    }
    return passphrase;
  }
  
  /**
   * Generates a random salt of the specified length.
   * @param length length of the salt to generate
   * @return salt
   */
  private byte[] randomSalt(int length) {
    byte[] salt = new byte[length];
    secureRandom.nextBytes(salt);
    return salt;
  }

}
