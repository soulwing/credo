/*
 * File created on Mar 20, 2014 
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link FacesFileDownloadResponse}.
 *
 * @author Carl Harris
 */
public class FacesFileDownloadResponseTest {

  private static final String FILE_NAME = "fileName";

  private static final String CHARSET = "charset";

  private static final String CONTENT_TYPE = "contentType";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private ExternalContext externalContext;
  
  @Mock
  private ResponseWriter writer;
  
  @Mock
  private ResponseStream outputStream;
  
  private FacesFileDownloadResponse response;
  
  @Before
  public void setUp() throws Exception {
    response = new FacesFileDownloadResponse(facesContext);
  }
  
  @Test
  public void testSetContentType() throws Exception {
    context.checking(fetchExternalContextExpectations());
    context.checking(new Expectations() { { 
      oneOf(externalContext).setResponseContentType(with(CONTENT_TYPE));
    } });
    
    response.setContentType(CONTENT_TYPE);
  }

  @Test
  public void testSetCharacterEncoding() throws Exception {
    context.checking(fetchExternalContextExpectations());
    context.checking(new Expectations() { { 
      oneOf(externalContext).setResponseCharacterEncoding(with(CHARSET));
    } });
    
    response.setCharacterEncoding(CHARSET);
  }
  
  @Test
  public void testSetFileName() throws Exception {
    context.checking(fetchExternalContextExpectations());
    context.checking(new Expectations() { { 
      oneOf(externalContext).setResponseHeader(
          with(FacesFileDownloadResponse.CONTENT_DISPOSITION_HEADER),
          with(allOf(
              containsString("attachment;"), 
              containsString("filename="),
              containsString(FILE_NAME))));
          
    } });
    
    response.setFileName(FILE_NAME);
  }

  @Test
  public void testGetWriter() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(facesContext).getResponseWriter();
      will(returnValue(writer));
    } });
    
    assertThat(response.getWriter(), is(sameInstance((Writer) writer)));
  }

  @Test
  public void testGetOutputStream() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(facesContext).getResponseStream();
      will(returnValue(outputStream));
    } });
    
    assertThat(response.getOutputStream(), 
        is(sameInstance((OutputStream) outputStream)));
  }
  
  private Expectations fetchExternalContextExpectations() {
    return new Expectations() { { 
      allowing(facesContext).getExternalContext();
      will(returnValue(externalContext));
    } };
  }
  
}
