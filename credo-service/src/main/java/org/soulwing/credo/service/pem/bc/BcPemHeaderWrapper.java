/*
 * File created on Feb 28, 2014 
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
package org.soulwing.credo.service.pem.bc;

import org.bouncycastle.util.io.pem.PemHeader;
import org.soulwing.credo.service.pem.PemHeaderWrapper;

/**
 * A {@link PemHeaderWrapper} implemented using Bouncy Castle.
 *
 * @author Carl Harris
 */
public class BcPemHeaderWrapper implements PemHeaderWrapper {

  private final PemHeader delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public BcPemHeaderWrapper(PemHeader delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return delegate.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStringValue() {
    return delegate.getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getIntValue() {
    return Integer.parseInt(delegate.getValue());
  }

}
