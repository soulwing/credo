/*
 * File created on Feb 21, 2014 
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
package org.soulwing.credo.facelets;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;

/**
 * Unit tests for {@link Credential} bean.
 *
 * @author Carl Harris
 */
public class CredentialBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock  
  private Credential credential;
  
  private CredentialBean bean;
  
  @Before
  public void setUp() {
    bean = new CredentialBean(credential);
  }
  
  @Test
  public void testGetTags() throws Exception {
    final Tag tag1 = context.mock(Tag.class, "tag1");
    final Tag tag2 = context.mock(Tag.class, "tag2");
    final Set<Tag> tags = new LinkedHashSet<>();
    tags.add(tag1);
    tags.add(tag2);
    context.checking(new Expectations() { { 
      oneOf(credential).getTags();
      will(returnValue(tags));
      oneOf(tag1).getText();
      will(returnValue("tag1"));
      oneOf(tag2).getText();
      will(returnValue("tag2"));
    } });
    
    assertThat(bean.getTags(), is(equalTo("tag1, tag2")));
  }
  
}
