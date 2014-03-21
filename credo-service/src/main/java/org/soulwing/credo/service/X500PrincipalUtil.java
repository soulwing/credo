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
package org.soulwing.credo.service;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

/**
 * Utility methods for handling X.500 principal names.
 *
 * @author Carl Harris
 */
public class X500PrincipalUtil {

  /**
   * Gets the common name (CN) component of an X.500 principal name.
   * @param principal a distinguished principal name
   * @return the common name (CN) component or a string representation of 
   *   {@code name} if the name does not have a common name component
   */
  public static String getCommonName(X500Principal principal) {
    String name = principal.getName();
    try {
      LdapName ldapName = new LdapName(name);
      for (Rdn rdn : ldapName.getRdns()) {
        if (rdn.getType().equalsIgnoreCase("cn")) {
          return rdn.getValue().toString();
        }
      }
      return name;
    }
    catch (NamingException ex) {
      return name;
    }
  }

}
