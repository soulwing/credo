/*
 * File created on Feb 26, 2014 
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;

/**
 * Unit tests for {@link ConcreteExportRequest}.
 *
 * @author Carl Harris
 */
public class ConcreteExportRequestTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Credential credential;
  
  private ConcreteExportRequest request;
  
  @Before
  public void setUp() throws Exception {
    request = new ConcreteExportRequest(credential);
  }
  
  @Test
  public void testGetFileName() throws Exception {
    final String fileName = "some.dotted.and  spaced name ";
    context.checking(new Expectations() { { 
      oneOf(credential).getName();
      will(returnValue(fileName));
    } });
    
    request.setFileName(null);
    request.setFormat(ExportFormat.PEM_ARCHIVE);
    String expectedName = "some_dotted_and_spaced_name" 
        + request.getFormat().getFileSuffix();
    assertThat(request.getFileName(), is(equalTo(expectedName)));
  }
  
}
