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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Tag;
import org.soulwing.credo.repository.TagRepository;

/**
 * Unit tests for {@link ConcreteTagService}.
 *
 * @author Carl Harris
 */
public class ConcreteTagServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private TagRepository tagRepository;
  
  @Mock
  private Tag tag;
  
  private ConcreteTagService service = new ConcreteTagService();
  
  @Before
  public void setUp() throws Exception {
    service.tagRepository = tagRepository;
  }
  
  @Test
  public void testFindAllTags() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(tagRepository).findAll();
      will(returnValue(Collections.singleton(tag)));
    } });
    
    assertThat(service.findAllTags(), contains(tag));
  }
}
