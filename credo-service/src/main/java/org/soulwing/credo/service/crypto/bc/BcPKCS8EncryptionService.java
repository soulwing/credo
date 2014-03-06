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
package org.soulwing.credo.service.crypto.bc;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang.Validate;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.jcajce.JcaPKCS8EncryptedPrivateKeyInfoBuilder;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.crypto.PKCS8EncryptionService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PKCS8EncryptionService} that encrypts/decrypts using PKCS8
 * and is implemented using Bouncy Castle.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class BcPKCS8EncryptionService
    implements PKCS8EncryptionService {

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
  public PrivateKeyWrapper encrypt(PrivateKeyWrapper privateKey,
      Password password) {
    Validate.notNull(password, "passphrase is required");
    PKCS8EncryptedPrivateKeyInfo encryptedKeyInfo = 
        new JcaPKCS8EncryptedPrivateKeyInfoBuilder(privateKey.derive())
            .build(createPrivateKeyEncryptor(password));
    return new BcPKCS8PrivateKeyWrapper(encryptedKeyInfo, keyFactory,
        objectBuilderFactory);
  }

  private OutputEncryptor createPrivateKeyEncryptor(Password password) {
    try {
      return new JceOpenSSLPKCS8EncryptorBuilder(
          PKCS8Generator.PBE_SHA1_3DES)
          .setPasssword(password.toCharArray())
          .setIterationCount(65536)
          .build();
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

}
