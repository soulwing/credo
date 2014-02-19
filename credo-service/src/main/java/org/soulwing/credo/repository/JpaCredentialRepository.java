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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;

/**
 * A {@link CredentialRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaCredentialRepository implements CredentialRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void add(Credential credential) {
    Set<Tag> tags = new LinkedHashSet<>();
    
    for (Tag tag : credential.getTags()) {
      tags.add(mergeIfNecessary(tag));
    }
    credential.setTags(tags);
    entityManager.persist(credential);
  }

  private Tag mergeIfNecessary(Tag tag) {
    try {
      tag = entityManager.merge(tag);
    }
    catch (IllegalArgumentException ex) {
      assert true;
    }
    return tag;
  }

}