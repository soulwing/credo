/*
 * File created on Mar 22, 2014 
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
package org.soulwing.credo.service.request;

import org.soulwing.credo.CredentialRequest;

/**
 * A wrapper for a credential request.
 *
 * @author Carl Harris
 */
public class CredentialRequestWrapper implements CredentialRequestDetail {

  private final CredentialRequest delegate;

  private boolean credentialCreated;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public CredentialRequestWrapper(CredentialRequest delegate) {
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
  public boolean isCredentialCreated() {
    return credentialCreated;
  }

  /**
   * Sets a flag indicating whether a credential has been created for 
   * the subject request.
   * @param credentialCreated the flag state to set
   */
  public void setCredentialCreated(boolean credentialCreated) {
    this.credentialCreated = credentialCreated;
  }

}
