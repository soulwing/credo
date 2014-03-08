/*
 * File created on Mar 7, 2014 
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

import java.security.NoSuchAlgorithmException;

import org.soulwing.credo.Password;


/**
 * A wrapper for a key store implementation.
 *
 * @author Carl Harris
 */
public interface KeyStoreBuilder {

  /**
   * Begins an entry.
   * @param alias alias name for the entry
   * @return the receiver
   */
  KeyStoreBuilder beginEntry(String alias);
  
  /**
   * Sets the private key for the entry in progress.
   * @param privateKey the private key to set
   * @return the receiver
   */
  KeyStoreBuilder setPrivateKey(PrivateKeyWrapper privateKey);
  
  /**
   * Sets the passphrase for the entry in progress.
   * @param passphrase the passphrase to set
   * @return the receiver
   */
  KeyStoreBuilder setPassphrase(Password passphrase);
  
  /**
   * Adds a certificate to the entry in progress.
   * @param certificate the certificate to add
   * @return the receiver
   */
  KeyStoreBuilder addCertificate(CertificateWrapper certificate);
  
  /**
   * Ends the entry in progress.
   * @return the receiver
   */
  KeyStoreBuilder endEntry();
  
  /**
   * Builds the key store.
   * @param passphrase a passphrase that will be used to protect the key store
   * @return a byte array encoding of the key store content
   * @throws NoSuchAlgorithmException
   */
  byte[] build(Password passphrase) throws NoSuchAlgorithmException;
  
}
