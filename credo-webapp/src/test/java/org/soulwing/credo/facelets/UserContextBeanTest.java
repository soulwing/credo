/*
 * File created on Mar 10, 2014 
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
import static org.jmock.Expectations.returnValue;
import static org.junit.Assert.assertThat;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link UserContextBean}.
 *
 * @author Carl Harris
 */
public class UserContextBeanTest {

  private static final String LOGIN_NAME = "someUser";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private ExternalContext externalContext;
  
  private UserContextBean bean = new UserContextBean();
  
  @Before
  public void setUp() throws Exception {
    bean.facesContext = facesContext;
  }
  
  @Test
  public void testGetLoginNameAndSetCache() {
    context.checking(remoteUserExpectations(returnValue(LOGIN_NAME)));
    assertThat(bean.getLoginName(), is(equalTo(LOGIN_NAME)));
    // this call should return cached value without looking at context
    assertThat(bean.getLoginName(), is(equalTo(LOGIN_NAME)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetLoginNameWhenNoRemoteUser() {
    context.checking(remoteUserExpectations(returnValue(null)));
    bean.getLoginName();
  }
  
  private Expectations remoteUserExpectations(final Action outcome) {
    return new Expectations() { { 
      oneOf(facesContext).getExternalContext();
      will(returnValue(externalContext));
      oneOf(externalContext).getRemoteUser();
      will(outcome);
    } };
  }
  
}
