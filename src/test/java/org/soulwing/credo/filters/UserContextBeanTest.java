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
package org.soulwing.credo.filters;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link UserContextBean}.
 *
 * @author Carl Harris
 */
public class UserContextBeanTest {

  private static final String LOGIN_NAME = "someUser";

  private UserContextBean bean = new UserContextBean();
  
  @Test
  public void testSetAndGetLoginName() {
    bean.setLoginName(LOGIN_NAME);
    assertThat(bean.getLoginName(), is(equalTo(LOGIN_NAME)));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetLoginNameWhenNoRemoteUser() {    
    bean.getLoginName();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNullRemoteUser() {    
    bean.setLoginName(null);
  }  

}
