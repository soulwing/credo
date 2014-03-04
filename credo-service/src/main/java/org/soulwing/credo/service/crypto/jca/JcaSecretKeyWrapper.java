/*
 * File created on Mar 3, 2014 
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

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link SecretKeyWrapper} that delegates to a JCA {@link SecretKey}.
 *
 * @author Carl Harris
 */
public class JcaSecretKeyWrapper implements SecretKeyWrapper {

  private final SecretKey delegate;
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * Constructs a new instance.
   * @param delegate JCA secret key delegate
   * @param objectBuilderFactory PEM object builder factory
   */
  public JcaSecretKeyWrapper(SecretKey delegate,
      PemObjectBuilderFactory objectBuilderFactory) {
    this.delegate = delegate;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPrivateKeyRequired() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey getPrivateKey() {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivateKey(PrivateKey publicKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    return objectBuilderFactory.newBuilder()
        .setType("RSA SECRET KEY")
        .append(delegate.getEncoded())
        .build().getEncoded();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKey derive() {
    return delegate;
  }

}
