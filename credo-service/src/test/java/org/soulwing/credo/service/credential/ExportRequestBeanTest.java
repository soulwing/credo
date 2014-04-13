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
package org.soulwing.credo.service.credential;

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
import org.soulwing.credo.service.credential.ExportRequestBean;

/**
 * Unit tests for {@link ExportRequestBean}.
 *
 * @author Carl Harris
 */
public class ExportRequestBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Credential credential;
  
  private ExportRequestBean request;
  
  @Before
  public void setUp() throws Exception {
    request = new ExportRequestBean(credential);
  }
  
  @Test
  public void testGetFileNameDefault() throws Exception {
    final String fileName = "some.dotted.and  spaced name ";
    context.checking(new Expectations() { { 
      oneOf(credential).getName();
      will(returnValue(fileName));
    } });
    
    request.setFileName(null);
    String expectedName = "some_dotted_and_spaced_name"; 
    assertThat(request.getFileName(), is(equalTo(expectedName)));
  }

  @Test
  public void testGetFileNameExplicit() throws Exception {
    String fileName = "some name";
    request.setFileName(fileName);
    assertThat(request.getFileName(), is(equalTo(fileName)));
  }

}
