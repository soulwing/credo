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
package org.soulwing.credo.service.x509.bc;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.soulwing.credo.service.x509.IncorrectPassphraseException;
import org.soulwing.credo.service.x509.PrivateKeyWrapper;

/**
 * A {@link PrivateKeyWrapper} implementation based on Bouncy Castle.
 * 
 * @author Carl Harris
 */
public class BcPrivateKeyWrapper implements BcWrapper, PrivateKeyWrapper {

  private final Object key;

  private char[] passphrase;
  
  /**
   * Constructs a new instance.
   * @param key key object
   */
  public BcPrivateKeyWrapper(Object key) {
    this.key = key;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    return key instanceof PKCS8EncryptedPrivateKeyInfo;
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
  public String getContent() throws IOException, IllegalStateException {
    StringWriter writer = new StringWriter();
    try (PEMWriter pemWriter = new PEMWriter(writer)) {
      PrivateKeyInfo privateKeyInfo = derivePrivateKeyInfo();
      if (passphrase == null) {
        pemWriter.writeObject(privateKeyInfo);
      }
      else {
        PKCS8Generator generator = new PKCS8Generator(privateKeyInfo, 
            createPrivateKeyEncryptor());
        pemWriter.writeObject(generator.generate());
      }
      pemWriter.flush();
      return writer.toString();
    }
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
      return decryptPrivateKey();
    }
    else if (key instanceof PEMKeyPair) {
      return ((PEMKeyPair) key).getPrivateKeyInfo();
    }
    else {
      throw new RuntimeException("unexpected key type " 
            + key.getClass().getName());
    }
  }

  private PrivateKeyInfo decryptPrivateKey() {
    try {
      return ((PKCS8EncryptedPrivateKeyInfo) key)
          .decryptPrivateKeyInfo(createPrivateKeyDecryptor());
    }
    catch (PKCSException ex) {
      throw new IncorrectPassphraseException();
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

  private InputDecryptorProvider createPrivateKeyDecryptor() {
    try {
      Validate.notNull(passphrase, "passphrase is required");
      return new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase);
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }


}
