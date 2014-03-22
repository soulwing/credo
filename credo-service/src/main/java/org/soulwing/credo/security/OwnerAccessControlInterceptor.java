/*
 * File created on Mar 22, 2014 
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
package org.soulwing.credo.security;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.soulwing.credo.Owned;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.UserGroupMember;
import org.soulwing.credo.logging.LoggerCategory;
import org.soulwing.credo.logging.LoggerCategory.Category;
import org.soulwing.credo.repository.UserGroupMemberRepository;
import org.soulwing.credo.security.Restricted.Restriction;
import org.soulwing.credo.service.UserContextService;

/**
 * An interceptor that validates the owner property of an object to 
 * ensure that the logged-in user is a member of the specified owner group.
 *
 * @author Carl Harris
 */
@Interceptor
@Restricted(Restriction.OWNER)
public class OwnerAccessControlInterceptor {

  @Inject 
  @LoggerCategory(Category.SECURITY)
  protected Logger logger;
  
  @Inject
  protected UserContextService userContextService;
  
  @Inject
  protected UserGroupMemberRepository memberRepository;
  
  @AroundInvoke
  @Transactional(Transactional.TxType.MANDATORY)
  public Object validateAccessAllowed(InvocationContext context) 
      throws Exception {
    for (Object parameter : context.getParameters()) {
      if (parameter instanceof Owned) {
        validateOwner((Owned) parameter);
      }
    }
    Object result = context.proceed();
    if (result instanceof Owned) {
      validateOwner((Owned) result);
    }
    return result;
  }

  private void validateOwner(Owned owned) throws OwnerAccessControlException {
    UserGroup owner = owned.getOwner();
    if (owner != null) {
      String loginName = userContextService.getLoginName();
      String groupName = owner.getName();
      if (logger.isTraceEnabled()) {
        logger.trace(String.format(
            "validating that user %s is a member of group %s for owned object %s",
            loginName, groupName, owned));
      }
      UserGroupMember member = memberRepository.findByGroupAndLoginName(
          owner, loginName);
      if (member == null) {
        logger.error(String.format(
            "user %s is not a member of group %s for owned object %s",
            loginName, groupName, owned));
        throw new OwnerAccessControlException(groupName, loginName);
      }
    }
  }
  
}
