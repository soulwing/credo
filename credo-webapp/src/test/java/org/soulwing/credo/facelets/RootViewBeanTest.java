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

import static org.junit.Assert.fail;

import javax.faces.context.FacesContext;

import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link RootViewBean}.
 *
 * @author Carl Harris
 */
public class RootViewBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private FacesContext facesContext;

  private RootViewBean bean = new RootViewBean();
  
  @Before
  public void setUp() throws Exception {
    bean.facesContext = facesContext;
  }
  
  @Test
  public void testRedirectWhenUserHasNoProfile() throws Exception {
    fail("not implemented");
  }
  
  @Test
  public void testRedirectWhenUserHasProfile() throws Exception {
    fail("not implemented");
  }
  
}
