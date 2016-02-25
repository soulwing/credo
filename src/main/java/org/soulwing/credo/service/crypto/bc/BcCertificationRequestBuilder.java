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

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.soulwing.credo.service.crypto.CertificationRequestBuilder;
import org.soulwing.credo.service.crypto.CertificationRequestException;
import org.soulwing.credo.service.crypto.CertificationRequestWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;

/**
 * A {@link CertificationRequestBuilder} that is based on the Bouncy Castle
 * {@link PKCS10CertificationRequestBuilder}.
 *
 * @author Carl Harris
 */
public class BcCertificationRequestBuilder
    implements CertificationRequestBuilder {

  private X500Principal subject;
  private PublicKeyWrapper publicKey;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CertificationRequestBuilder setSubject(X500Principal subject) {
    this.subject = subject;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CertificationRequestBuilder setPublicKey(PublicKeyWrapper publicKey) {
    this.publicKey = publicKey;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CertificationRequestWrapper build(PrivateKeyWrapper privateKey)
      throws CertificationRequestException {
    PKCS10CertificationRequestBuilder builder = 
        new JcaPKCS10CertificationRequestBuilder(subject, 
            publicKey.derive());
    JcaContentSignerBuilder signerBuilder = 
        new JcaContentSignerBuilder("SHA1WithRSA");
    try {
      ContentSigner signer = signerBuilder.build(privateKey.derive());
      PKCS10CertificationRequest csr = builder.build(signer);
      return new BcCertificationRequestWrapper(csr);
    }
    catch (OperatorCreationException ex) {
      throw new CertificationRequestException(ex);
    }
  }

}
