/*
 * File created on Feb 20, 2014 
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
package org.soulwing.credo.service;

import java.util.Date;
import java.util.List;

import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * An object that represents the details of a credential that has been
 * fully validated for import.
 *
 * @author Carl Harris
 */
public interface ImportDetails extends CredentialEditor {

  /**
   * Gets the private key.
   * @return private key
   */
  PrivateKeyWrapper getPrivateKey();
  
  /**
   * Gets the certificate chain.
   * @return certificate chain
   */
  List<CertificateWrapper> getCertificates();
  
  /**
   * Gets the {@code subject} property.
   * @return
   */
  String getSubject();

  /**
   * Gets the common name component of the subject.
   * @return common name component or the full subject name if the name
   *    does not contain a common name component
   */
  String getSubjectCommonName();
  
  /**
   * Gets the {@code issuer} property.
   * @return
   */
  String getIssuer();

  /**
   * Gets the common name component of the issuer.
   * @return common name component or the full issuer name if the name
   *    does not contain a common name component
   */
  String getIssuerCommonName();

  /**
   * Gets the {@code serialNumber} property.
   * @return
   */
  String getSerialNumber();

  /**
   * Gets the {@code notBefore} property.
   * @return
   */
  Date getNotBefore();

  /**
   * Gets the {@code notAfter} property.
   * @return
   */
  Date getNotAfter();
  
}