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
package org.soulwing.credo.service.crypto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.soulwing.crypt4j.Crypt;

/**
 * A {@link PasswordEncryptionService} based on Crypt4j.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class Crypt4jPasswordEncryptionService
    implements PasswordEncryptionService {

  static final String SALT_PREFIX = "$6$rounds=10000$";
  
  static final int SALT_LENGTH = 16;
  
  private static final String SALT_SET = 
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz./";
  
  private SecureRandom secureRandom;
  
  @PostConstruct
  public void init() {
    try {
      secureRandom = SecureRandom.getInstance("SHA1PRNG");
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String encrypt(char[] password) {
    try {
      return Crypt.crypt(password, randomSalt(SALT_LENGTH));
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(char[] presented, String expected) {
    try {
      String actual = Crypt.crypt(presented, expected);
      return expected.equals(actual);
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Creates a random salt of the specified length.
   * @param length the length of the salt to create
   * @return salt
   */
  private String randomSalt(int length) {    
    StringBuilder sb = new StringBuilder();
    sb.append(SALT_PREFIX);
    while (length-- > 0) {
      sb.append(SALT_SET.charAt(secureRandom.nextInt(SALT_SET.length())));
    }    
    return sb.toString();
  }

}
