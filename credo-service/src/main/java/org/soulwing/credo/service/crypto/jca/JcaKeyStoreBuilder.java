/*
 * File created on Mar 8, 2014 
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.soulwing.credo.Password;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.KeyStoreBuilder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A {@link KeyStoreBuilder} that builds a JCA {@link KeyStore}.
 *
 * @author Carl Harris
 */
public class JcaKeyStoreBuilder implements KeyStoreBuilder {

  private final KeyStore keyStore;
  
  private KeyStoreEntry entry;
  
  /**
   * Constructs a new instance.
   * @param keyStore the keystore to build
   */
  public JcaKeyStoreBuilder(KeyStore keyStore) {
    this.keyStore = keyStore;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public KeyStoreBuilder beginEntry(String alias) {
    assertEntryInProgress(false);
    entry = new KeyStoreEntry(alias);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyStoreBuilder setPrivateKey(PrivateKeyWrapper privateKey) {
    assertEntryInProgress(true);
    entry.setPrivateKey(privateKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyStoreBuilder setPassphrase(Password passphrase) {
    assertEntryInProgress(true);
    entry.setPassphrase(passphrase);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyStoreBuilder addCertificate(CertificateWrapper certificate) {
    assertEntryInProgress(true);
    entry.addCertificate(certificate);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public KeyStoreBuilder endEntry() {
    assertEntryInProgress(true);
    try {
      entry.putEntry(keyStore);
      entry = null;
      return this;
    }
    catch (KeyStoreException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] build(Password passphrase) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      keyStore.store(outputStream, passphrase.toCharArray());
      outputStream.flush();
      return outputStream.toByteArray();
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
    catch (KeyStoreException ex) {
      throw new RuntimeException(ex);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }   
  }

  private void assertEntryInProgress(boolean state) {
    if ((entry != null) != state) {
      throw new IllegalStateException("illegal entry state");
    }
  }
  
  static class KeyStoreEntry {
    
    private final String alias;
    private final List<CertificateWrapper> certificates = new ArrayList<>();

    private PrivateKeyWrapper privateKey;
    private Password passphrase;
    
    public KeyStoreEntry(String alias) {
      this.alias = alias;
    }
    
    public void setPrivateKey(PrivateKeyWrapper privateKey) {
      this.privateKey = privateKey;
    }
    
    public void setPassphrase(Password passphrase) {
      this.passphrase = passphrase;
    }
    
    public void addCertificate(CertificateWrapper certificate) {
      certificates.add(certificate);
    }
    
    public void putEntry(KeyStore keyStore) throws KeyStoreException {
      keyStore.setKeyEntry(alias, privateKey.derive(), 
          passphrase.toCharArray(), createCertificateChain());
    }

    private Certificate[] createCertificateChain() {
      Certificate[] chain = new Certificate[certificates.size()];
      for (int i = 0; i < chain.length; i++) {
        chain[i] = certificates.get(i).derive();
      }
      return chain;
    }
  }

}
