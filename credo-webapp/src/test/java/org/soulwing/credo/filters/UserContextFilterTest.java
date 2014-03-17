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
package org.soulwing.credo.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.MutableUserContextService;

/**
 * Unit tests for {@link UserContextFilter}.
 *
 * @author Carl Harris
 */
public class UserContextFilterTest {

  private static final String LOGIN_NAME = "someUser";
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private MutableUserContextService userContextService;
  
  @Mock
  private HttpServletRequest request;
  
  @Mock
  private HttpServletResponse response;
  
  @Mock
  private FilterChain filterChain;
  
  private UserContextFilter filter = new UserContextFilter();
  
  @Before
  public void setUp() throws Exception {
    filter.userContextService = userContextService;
  }
  
  @Test
  public void testDoFilterWhenRemoteUserAvailable() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(request).getRemoteUser();
      will(returnValue(LOGIN_NAME));
      oneOf(userContextService).setLoginName(with(LOGIN_NAME));
      oneOf(filterChain).doFilter(with(same(request)), with(same(response)));
    } });
    
    filter.doFilter(request, response, filterChain);
  }

  @Test
  public void testDoFilterWhenRemoteUserNotAvailable() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(request).getRemoteUser();
      will(returnValue(null));
      oneOf(filterChain).doFilter(with(same(request)), with(same(response)));
    } });
    
    filter.doFilter(request, response, filterChain);
  }

  @Test(expected = ServletException.class)
  public void testDoFilterWhenRemoteUserChangesInSameSession() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(request).getRemoteUser();
      will(onConsecutiveCalls(returnValue(LOGIN_NAME), 
          returnValue(LOGIN_NAME + "2")));
      oneOf(userContextService).setLoginName(with(LOGIN_NAME));
      oneOf(userContextService).setLoginName(with(LOGIN_NAME + "2"));
      will(throwException(new IllegalArgumentException()));
      oneOf(filterChain).doFilter(with(same(request)), with(same(response)));
    } });
    
    filter.doFilter(request, response, filterChain);
    filter.doFilter(request, response, filterChain);
  }

}
