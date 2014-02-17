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

import org.soulwing.credo.Tag;

/**
 * A repository of persistent {@link Tag} entities.
 *
 * @author Carl Harris
 */
public interface TagRepository {

  /**
   * Creates a new transient tag instance.
   * @param text text for the tag.
   * @return tag instance
   */
  Tag newInstance(String text);
  
  /**
   * Finds a tag by searching for an exact match of its text.
   * @param text the text to search
   * @return tag or {@code null} if no matching tag was found
   */
  Tag findByTagText(String text);
  
}
