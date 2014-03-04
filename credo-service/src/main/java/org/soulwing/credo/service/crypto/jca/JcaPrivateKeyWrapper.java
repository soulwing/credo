/*
 * File created on Mar 2, 2014 
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

import java.io.IOException;
import java.security.PrivateKey;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PrivateKeyWrapper} that delegates to a JCA {@link PrivateKey}.
 * 
 * @author Carl Harris
 */
public class JcaPrivateKeyWrapper implements PrivateKeyWrapper {

  private final PrivateKey delegate;
  private final PemObjectBuilderFactory objectBuilderFactory;

  /**
   * Constructs a new instance.
   * @param privateKey private key delegate
   * @param objectBuilderFactory PEM object builder factory
   */
  public JcaPrivateKeyWrapper(PrivateKey privateKey,
      PemObjectBuilderFactory objectBuilderFactory) {
    this.delegate = privateKey;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isProtected() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getProtectionParameter() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProtectionParameter(Object parameter) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    try {
      PrivateKeyInfo keyInfo = PrivateKeyInfo.getInstance(
          delegate.getEncoded());
      byte[] content = keyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
      return objectBuilderFactory.newBuilder().setType("RSA PRIVATE KEY")
          .append(content).build().getEncoded();
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
    return delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper deriveWrapper() {
    return new JcaPrivateKeyWrapper(derive(), objectBuilderFactory);
  }

}
