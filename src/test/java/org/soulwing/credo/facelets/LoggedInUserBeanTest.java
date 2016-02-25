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
package org.soulwing.credo.facelets;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.UserProfile;
import org.soulwing.credo.service.UserProfileService;

/**
 * Units for {@link LoggedInUserBean}.
 *
 * @author Carl Harris
 */
public class LoggedInUserBeanTest {

  private static final String FULL_NAME = "Full Name";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private UserProfileService profileService;
  
  @Mock
  private UserProfile profile;
  
  private LoggedInUserBean bean = new LoggedInUserBean();
  
  @Before
  public void setUp() throws Exception {
    bean.profileService = profileService;
  }
  
  @Test
  public void testInitAndGetName() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(profileService).getLoggedInUserProfile();
      will(returnValue(profile));
      allowing(profile).getFullName();
      will(returnValue(FULL_NAME));
    } });
    
    bean.init();
    assertThat(bean.getName(), is(equalTo(FULL_NAME)));
  }
}
