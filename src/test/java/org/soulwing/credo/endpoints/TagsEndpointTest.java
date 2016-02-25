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
package org.soulwing.credo.endpoints;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Tag;
import org.soulwing.credo.service.TagService;

/**
 * Unit tests for {@link TagsEndpoint}.
 *
 * @author Carl Harris
 */
public class TagsEndpointTest {

  private static final String TAG_TEXT = "tagText";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private TagService tagService;
  
  @Mock
  private Tag tag;
  
  private TagsEndpoint endpoint = new TagsEndpoint();
  
  @Before
  public void setUp() throws Exception {
    endpoint.tagService = tagService;
  }
  
  @Test
  public void testGetTags() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(tagService).findAllTags();
      will(returnValue(Collections.singleton(tag)));
      oneOf(tag).getText();
      will(returnValue(TAG_TEXT));
    } });
    
    assertThat(endpoint.getTags(), contains(TAG_TEXT));
  }
}
