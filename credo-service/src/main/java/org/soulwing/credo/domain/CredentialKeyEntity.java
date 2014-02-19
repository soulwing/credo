/*
 * File created on Feb 18, 2014 
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
package org.soulwing.credo.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.soulwing.credo.CredentialKey;

/**
 * A {@link CredentialKey} implemented as a JPA entity.
 *
 * @author Carl Harris
 */
@Entity
@Table(name = "credential_key")
public class CredentialKeyEntity extends CredentialComponentEntity 
    implements CredentialKey {

  private static final long serialVersionUID = 1989015823707931759L;

}
