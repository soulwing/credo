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
package org.soulwing.credo.service.importer;

import java.util.Date;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.x509.CertificateWrapper;

/**
 * A concrete implementation of {@link ImportDetails}.
 *
 * @author Carl Harris
 */
public class ConcreteImportDetails implements ImportDetails {

  private static final long serialVersionUID = -5190604068859062839L;

  private final String subject;
  private final String issuer;
  private final String serialNumber;
  private final Date notBefore;
  private final Date notAfter;
  
  protected ConcreteImportDetails(CertificateWrapper certificate) {
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
   * {@inheritDoc}
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuer() {
    return issuer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSerialNumber() {
    return serialNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotBefore() {
    return notBefore;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotAfter() {
    return notAfter;
  }

}
