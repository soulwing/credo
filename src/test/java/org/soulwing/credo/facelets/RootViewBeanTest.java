/*
 * File created on Mar 2, 2014 
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

import static org.hamcrest.Matchers.endsWith;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.WelcomeService;
import org.soulwing.credo.testing.JUnitRuleClassImposterizingMockery;

/**
 * Unit tests for {@link RootViewBean}.
 *
 * @author Carl Harris
 */
public class RootViewBeanTest {

  private static final String CONTEXT_PATH = "/context";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();
  
  @Mock
  private FacesContext facesContext;

  @Mock
  private ExternalContext externalContext;
  
  @Mock
  private WelcomeService welcomeService;
  
  private RootViewBean bean = new RootViewBean();
  
  @Before
  public void setUp() throws Exception {
    bean.facesContext = facesContext;
    bean.welcomeService = welcomeService;
  }
  
  @Test
  public void testRedirectNewUser() throws Exception {
    context.checking(newContextExpectations(true));
    context.checking(new Expectations() { { 
      oneOf(externalContext).redirect(
          with(endsWith(RootViewBean.NEW_USER_PATH)));
      oneOf(facesContext).responseComplete();
    } });
    
    bean.redirect();
  }
  
  @Test
  public void testRedirectExistingUser() throws Exception {
    context.checking(newContextExpectations(false));
    context.checking(new Expectations() { { 
      oneOf(externalContext).redirect(
          with(CONTEXT_PATH + RootViewBean.EXISTING_USER_PATH));
      oneOf(facesContext).responseComplete();
    } });
    
    bean.redirect();
  }

  @Test(expected = RuntimeException.class)
  public void testRedirectThrowsIOException() throws Exception {
    context.checking(newContextExpectations(true));
    context.checking(new Expectations() { { 
      oneOf(externalContext).redirect(with(any(String.class)));
      will(throwException(new IOException()));
    } });
    
    bean.redirect();
  }
  
  /**
   * Creates expectations for obtaining context information that are common
   * to all tests.
   * @return expectations
   */
  private Expectations newContextExpectations(final boolean newUser) {
    return new Expectations() { { 
      allowing(facesContext).getExternalContext();
      will(returnValue(externalContext));
      oneOf(externalContext).getRequestContextPath();
      will(returnValue(CONTEXT_PATH));
      oneOf(welcomeService).isNewUser();
      will(returnValue(newUser));
    } };
  }

}
