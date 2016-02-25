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
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * A wrapper for an X.509 certificate object implementation.
 *
 * @author Carl Harris
 */
public interface CertificateWrapper {

  /**
   * Gets the subject name of this certificate.
   * @return subject name
   */
  X500Principal getSubject();
  
  /**
   * Gets the issuer name of this certificate.
   * @return issuer name
   */
  X500Principal getIssuer();
  
  /**
   * Gets the serial number of this certificate.
   * @return serial number
   */
  BigInteger getSerialNumber();
  
  /**
   * Gets the not-before validity date of this certificate.
   * @return date
   */
  Date getNotBefore();
  
  /**
   * Gets the not-after validity date of this certificate.
   * @return date
   */
  Date getNotAfter();
  
  /**
   * Tests whether this certificate is self-signed.
   * @return {@code true} if self-signed
   */
  boolean isSelfSigned();
  
  /**
   * Gets the content of this certificate in a suitable string encoding
   * (typically PEM).
   * @return certificate content
   * @throws IOException
   */
  String getContent() throws IOException;
  
  /**
   * Derives a JCA {@link Certificate} for the certificate represented by
   * this wrapper.
   * @return JCA certificate
   */
  Certificate derive();
  
}
