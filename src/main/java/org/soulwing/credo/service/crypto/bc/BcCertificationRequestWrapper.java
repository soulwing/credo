/*
 * File created on Mar 21, 2014 
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
import java.io.StringWriter;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.soulwing.credo.service.crypto.CertificationRequestWrapper;

/**
 * A {@link CertificationRequestWrapper} that wraps a Bouncy Castle
 * {@link PKCS10CertificationRequest}.
 *
 * @author Carl Harris
 */
public class BcCertificationRequestWrapper
    implements CertificationRequestWrapper {

  private final PKCS10CertificationRequest csr;
   
  /**
   * Constructs a new instance.
   * @param csr PKCS#10 certification request delegate
   */
  public BcCertificationRequestWrapper(PKCS10CertificationRequest csr) {
    this.csr = csr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getSubject() {
    return new X500Principal(csr.getSubject().toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() throws IOException {
    StringWriter writer = new StringWriter();
    try (PEMWriter pemWriter = new PEMWriter(writer)) {
      pemWriter.writeObject(csr);
      pemWriter.flush();
      return writer.toString();
    }
  }

}
