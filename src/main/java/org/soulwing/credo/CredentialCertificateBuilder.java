/*
 * File created on Feb 18, 2014 
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
package org.soulwing.credo;

import java.math.BigInteger;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * A builder for that produces a {@link CredentialCertificate}.
 *
 * @author Carl Harris
 */
public interface CredentialCertificateBuilder {

  /**
   * Sets the subject X.500 name for the certificate.
   * @param name subject name
   * @return the receiver
   */
  CredentialCertificateBuilder setSubject(X500Principal name);
  
  /**
   * Sets the subject name for the certificate.
   * @param name issuer name
   * @return the receiver
   */
  CredentialCertificateBuilder setIssuer(X500Principal name);
  
  /**
   * Sets the certificate's serial number
   * @param serialNumber the serial number to set
   * @return the receiver
   */
  CredentialCertificateBuilder setSerialNumber(BigInteger serialNumber);
  
  /**
   * Sets the certificate's not-before validity date.
   * @param notBefore the date to set
   * @return the receiver
   */
  CredentialCertificateBuilder setNotBefore(Date notBefore);
  
  /**
   * Sets the certificate's not-after validity date.
   * @param notAfter the date to set
   * @return the receiver
   */
  CredentialCertificateBuilder setNotAfter(Date notAfter);
  
  /**
   * Sets the certificate's PEM-encoded content.
   * @param content the content to set
   * @return the receiver
   */
  CredentialCertificateBuilder setContent(String content);
  
  /**
   * Builds a {@link CredentialCertificate} according to the current state
   * of the receiver.
   * @return certificate object
   * @throws IllegalStateException if the current state of the receiver is
   *    incomplete/inconsistent
   */
  CredentialCertificate build();
  
}
