/*
 * File created on Apr 13, 2014 
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
 * A {@link CredentialRequestEditor} that delegates to an underlying 
 * {@link CredentialRequest}.
 *
 * @author Carl Harris
 */
public interface DelegatingRequestEditor extends CredentialRequestEditor {

  /**
   * Gets the request delegate.
   * @return delegate
   */
  CredentialRequest getDelegate();
  
  /**
   * Sets the request delegate.
   * @param delegate the delegate to set
   */
  void setDelegate(CredentialRequest delegate);
  
}
