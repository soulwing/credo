/*
 * File created on Feb 19, 2014 
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

import java.io.IOException;
import java.security.PrivateKey;

import org.apache.commons.lang.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilder;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PrivateKeyWrapper} implementation based on Bouncy Castle.
 * 
 * @author Carl Harris
 */
public class BcPrivateKeyWrapper implements BcWrapper, PrivateKeyWrapper {

  private final Object key;
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  private char[] passphrase;
  
  /**
   * Constructs a new instance.
   * @param key key object
   * @param objectBuilderFactory PEM object builder factory
   */
  public BcPrivateKeyWrapper(Object key, 
      PemObjectBuilderFactory objectBuilderFactory) {
    this.key = key;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    return key instanceof PKCS8EncryptedPrivateKeyInfo
        || key instanceof PEMEncryptedKeyPair;    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassphrase() {
    return passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassphrase(char[] passphrase) {
    this.passphrase = passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    try {
      PrivateKeyInfo privateKeyInfo = derivePrivateKeyInfo();
      PemObjectBuilder builder = objectBuilderFactory.newBuilder();
      if (passphrase == null) {
        builder.setType("RSA PRIVATE KEY");
        builder.append(privateKeyInfo.getEncoded());
      }
      else {
        PKCS8Generator generator = new PKCS8Generator(privateKeyInfo, 
            createPrivateKeyEncryptor());
        builder.setType("ENCRYPTED PRIVATE KEY");
        builder.append(generator.generate().getContent());
      }
      
      return builder.build().getEncoded();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey derive() {
    throw new UnsupportedOperationException();
  }

  public AsymmetricKeyParameter derivePrivateKeyParameters() {
    try {      
      return PrivateKeyFactory.createKey(derivePrivateKeyInfo());
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private PrivateKeyInfo derivePrivateKeyInfo() {
    if (key instanceof PKCS8EncryptedPrivateKeyInfo) {
      return decryptPKCS8PrivateKey();
    }
    else if (key instanceof PEMEncryptedKeyPair) {
      return decryptPEMPrivateKey();
    }
    else if (key instanceof PEMKeyPair) {
      return ((PEMKeyPair) key).getPrivateKeyInfo();
    }
    else {
      throw new RuntimeException("unexpected key type " 
            + key.getClass().getName());
    }
  }

  private PrivateKeyInfo decryptPKCS8PrivateKey() {
    try {
      return ((PKCS8EncryptedPrivateKeyInfo) key)
          .decryptPrivateKeyInfo(createPKCS8KeyDecryptor());
    }
    catch (PKCSException ex) {
      throw new IncorrectPassphraseException();
    }
  }

  private PrivateKeyInfo decryptPEMPrivateKey() {
    try {
      PEMKeyPair keyPair = ((PEMEncryptedKeyPair) key)
          .decryptKeyPair(createPEMKeyDecryptor());
      return keyPair.getPrivateKeyInfo();
    }
    catch (EncryptionException ex) {
      throw new IncorrectPassphraseException();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private OutputEncryptor createPrivateKeyEncryptor() {
    try {
      Validate.notNull(passphrase, "passphrase is required");
      return new JceOpenSSLPKCS8EncryptorBuilder(
          PKCS8Generator.PBE_SHA1_3DES)
          .setPasssword(passphrase)
          .setIterationCount(100)
          .build();
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private InputDecryptorProvider createPKCS8KeyDecryptor() {
    try {
      Validate.notNull(passphrase, "passphrase is required");
      return new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase);
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private PEMDecryptorProvider createPEMKeyDecryptor() {
    Validate.notNull(passphrase, "passphrase is required");
    return new JcePEMDecryptorProviderBuilder().build(passphrase);
  }

}
