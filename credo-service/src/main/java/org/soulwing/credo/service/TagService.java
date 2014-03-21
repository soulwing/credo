/*
 * File created on Mar 17, 2014 
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
package org.soulwing.credo.service;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Local;

import org.soulwing.credo.Tag;

/**
 * A service that provides access to tags used to label credentials.
 *
 * @author Carl Harris
 */
@Local
public interface TagService {

  /**
   * Finds all currently defined tags.
   * @return collection of tags.
   */
  Collection<Tag> findAllTags();

  /**
   * Resolves the tag strings in the given array into a set of {@link Tag}
   * objects.
   * @param tags tag strings
   * @return tag set
   */
  Set<? extends Tag> resolve(String[] tags);
  
}
