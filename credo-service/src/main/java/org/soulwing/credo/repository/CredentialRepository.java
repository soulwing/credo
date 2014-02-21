/*
 * File created on Feb 16, 2014 
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
package org.soulwing.credo.repository;

import java.util.List;

import org.soulwing.credo.Credential;

/**
 * A repository of persistent {@link Credential} objects.
 *
 * @author Carl Harris
 */
public interface CredentialRepository {

  /**
   * Adds a credential to the repository.
   * @param credential the credential to add
   */
  void add(Credential credential);

  /**
   * Finds all credentials in the repository.
   * @return list of credentials
   */
  List<Credential> findAll();
  
}
