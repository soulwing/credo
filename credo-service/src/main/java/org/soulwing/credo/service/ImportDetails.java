/*
 * File created on Feb 20, 2014 
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

import java.io.Serializable;
import java.util.Date;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.soulwing.credo.service.x509.CertificateWrapper;

/**
 * An object that represents the details of a credential that has been
 * fully validated for import.
 *
 * @author Carl Harris
 */
public class ImportDetails implements Serializable {

  private static final long serialVersionUID = -5190604068859062839L;

  private final String subject;
  private final String issuer;
  private final String serialNumber;
  private final Date notBefore;
  private final Date notAfter;
  
  protected ImportDetails(CertificateWrapper certificate) {
    this.subject = getCommonName(certificate.getSubject().getName());
    this.issuer = getCommonName(certificate.getIssuer().getName());
    this.serialNumber = certificate.getSerialNumber().toString();
    this.notBefore = certificate.getNotBefore();
    this.notAfter = certificate.getNotAfter();
  }

  private String getCommonName(String name) {
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
  
  /**
   * Gets the {@code subject} property.
   * @return
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the {@code issuer} property.
   * @return
   */
  public String getIssuer() {
    return issuer;
  }

  /**
   * Gets the {@code serialNumber} property.
   * @return
   */
  public String getSerialNumber() {
    return serialNumber;
  }

  /**
   * Gets the {@code notBefore} property.
   * @return
   */
  public Date getNotBefore() {
    return notBefore;
  }

  /**
   * Gets the {@code notAfter} property.
   * @return
   */
  public Date getNotAfter() {
    return notAfter;
  }

}
