/*
 * File created on Mar 20, 2014 
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

import java.util.Set;

import org.soulwing.credo.Tag;
import org.soulwing.credo.service.CredentialRequestEditor;

/**
 * A configurable {@link CredentialRequestEditor}.
 *
 * @author Carl Harris
 */
public interface ConfigurableRequestEditor extends CredentialRequestEditor {

  /**
   * Sets the unique identifier of a credential to associate with the request.
   * <p>
   * This property is used when creating a request to renew an existing 
   * credential.
   * 
   * @param credentialId the credential identifier to set
   */
  void setCredentialId(Long credentialId);
  
  /**
   * Sets the tags for the request.
   * @param tags set of tags
   */
  void setTags(Set<? extends Tag> tags);
  
}
