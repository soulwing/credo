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

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.soulwing.credo.Tag;
import org.soulwing.credo.domain.TagEntity;

/**
 * A {@link TagRepository} implemented using JPA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JpaTagRepository implements TagRepository {

  @PersistenceContext
  protected EntityManager entityManager;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Tag newTag(String text) {
    TagEntity tag = new TagEntity();
    tag.setText(text);
    return tag;
  }

  @Override
  public Collection<Tag> findAll() {
    TypedQuery<Tag> query = entityManager.createNamedQuery("findAllTags", 
        Tag.class);
    return query.getResultList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Tag findByTagText(String text) {
    TypedQuery<TagEntity> query = 
        entityManager.createNamedQuery("findTagByText", TagEntity.class);
    query.setParameter("text", text);
    try {
      return query.getSingleResult();
    }
    catch (NoResultException ex) {
      return null;
    }
  }

}
