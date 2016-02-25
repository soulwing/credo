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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.UserProfilePreparation;
import org.soulwing.credo.service.UserProfileService;
import org.soulwing.credo.testing.JUnitRuleClassImposterizingMockery;

/**
 * Unit tests for {@link CreateUserProfileBean}.
 *
 * @author Carl Harris
 */
public class CreateUserProfileBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();
  
  @Mock
  private FacesContext facesContext;
  
  @Mock
  private ExternalContext externalContext;
  
  @Mock
  private UserProfileService userProfileService;
  
  @Mock
  private UserProfilePreparation preparation;
  
  private CreateUserProfileBean bean = new CreateUserProfileBean();
 
  @Before
  public void setUp() throws Exception {
    bean.facesContext = facesContext;
    bean.userProfileService = userProfileService;
  }
  
  @Test
  public void testInit() throws Exception {
    final String loginName = "someUser";
    context.checking(new Expectations() { {
      oneOf(facesContext).getExternalContext();
      will(returnValue(externalContext));
      oneOf(externalContext).getRemoteUser();
      will(returnValue(loginName));
      oneOf(userProfileService).prepareProfile(with(same(loginName)));
      will(returnValue(preparation));
    } });
    
    bean.init();
    assertThat(bean.getPreparation(), is(sameInstance(preparation)));
  }
  
  @Test
  public void testCreateProfile() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(userProfileService).createProfile(with(same(preparation)));
    } });
    
    bean.setPreparation(preparation);
    assertThat(bean.createProfile(), 
        is(equalTo(CreateUserProfileBean.SUCCESS_OUTCOME_ID)));
  }
  
  @Test(expected = RuntimeException.class)
  public void testCreateProfileWhenLoginNameExists() {
    context.checking(new Expectations() { { 
      oneOf(userProfileService).createProfile(with(same(preparation)));
      will(throwException(new RuntimeException()));
    } });
    
    bean.setPreparation(preparation);
    bean.createProfile();
  }

  @Test
  public void testCancel() throws Exception {
    assertThat(bean.cancel(), is(
        equalTo(CreateUserProfileBean.CANCEL_OUTCOME_ID)));
  }
  
}
