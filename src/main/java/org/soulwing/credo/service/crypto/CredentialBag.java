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
package org.soulwing.credo.service.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A bag of credential objects (e.g. private keys, certificates).
 *
 * @author Carl Harris
 */
public interface CredentialBag {

  /**
   * Adds all of the credential objects that appear on the given input
   * stream to the receiver.
   * @param inputStream the input stream to read
   * @return number of objects that were read from {@code inputStream}
   * @throws IOException
   */
  int addAllObjects(InputStream inputStream) throws IOException;
  
  /**
   * Attempts to find a private key object in the receiver.
   * @return private key or {@code null} if not found
   */
  PrivateKeyWrapper findPrivateKey();
  
  /**
   * Attempts to find a certificate whose public key information matches
   * the given private key.
   * @param privateKey the private key to match
   * @return matching certificate or {@code null} if not found
   * @throws UnsupportedKeyTypeException if {@code privateKey} is not of
   *    a supported type for certificate matching
   * @throws IncorrectPassphraseException if {@code privateKey} is encrypted
   *    and the configured passphrase is incorrect
   */
  CertificateWrapper findSubjectCertificate(PrivateKeyWrapper privateKey)
      throws UnsupportedKeyTypeException, IncorrectPassphraseException;
  
  /**
   * Attempts to find the chain of authority certificates for the given
   * certificate.
   * @param certificate the subject certificate
   * @return ordered list of authority certificates; the returned list may
   *    be empty or may not end in a self-signed certificate 
   */
  List<CertificateWrapper> findAuthorityCertificates(
      CertificateWrapper certificate);
  
  /**
   * Removes a given object from the receiver.
   * @param object the object to remove
   * @return {@code true} if an object was actually removed
   */
  boolean removeObject(Object object);
  
  /**
   * Tests whether a private key in the receiver requires a passphrase.
   * @return {@code true} if a passphrase is required
   */
  boolean isPassphraseRequired();
}
