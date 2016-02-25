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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.soulwing.credo.service.MutableUserContextService;

/**
 * A {@link Filter} that sets the logged in user context.
 *
 * @author Carl Harris
 */
@WebFilter
public class UserContextFilter implements Filter {

  @Inject
  protected MutableUserContextService userContextService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void destroy() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      updateContextService((HttpServletRequest) request);
    }
    filterChain.doFilter(request, response);
  }

  private void updateContextService(HttpServletRequest request) 
      throws ServletException {
    String loginName = request.getRemoteUser();
    if (loginName != null) {
      try {
        userContextService.setLoginName(loginName);
      }
      catch (IllegalArgumentException ex) {
        throw new ServletException(ex);
      }
    }
  }

}
