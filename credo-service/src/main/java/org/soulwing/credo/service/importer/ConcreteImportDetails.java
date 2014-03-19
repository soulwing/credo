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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.ImportDetails;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * A concrete implementation of {@link ImportDetails}.
 *
 * @author Carl Harris
 */
public class ConcreteImportDetails implements ImportDetails, Serializable {

  private static final long serialVersionUID = -5190604068859062839L;

  private final String subject;
  private final String issuer;
  private final String serialNumber;
  private final Date notBefore;
  private final Date notAfter;
  private final PrivateKeyWrapper privateKey;
  private final List<CertificateWrapper> certificates = new ArrayList<>();
  
  private String name;
  private String owner = UserGroup.SELF_GROUP_NAME;
  private String note;
  private String[] tags;

  public ConcreteImportDetails(PrivateKeyWrapper privateKey,
      CertificateWrapper certificate, 
      List<CertificateWrapper> authorities) {
    this.subject = certificate.getSubject().getName();
    this.issuer = certificate.getIssuer().getName();
    this.serialNumber = certificate.getSerialNumber().toString();
    this.notBefore = certificate.getNotBefore();
    this.notAfter = certificate.getNotAfter();
    this.privateKey = privateKey;
    this.certificates.add(certificate);
    this.certificates.addAll(authorities);
    this.name = getCommonName(certificate.getSubject().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper getPrivateKey() {
    return privateKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<CertificateWrapper> getCertificates() {
    return certificates;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public String getSubjectCommonName() {
    return getCommonName(subject);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuer() {
    return issuer;
  }

  @Override
  public String getIssuerCommonName() {
    return getCommonName(issuer);
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

  @Override
  public Date getExpiration() {
    return getNotAfter();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getOwner() {
    return owner;
  }

  @Override
  public void setOwner(String owner) {
    this.owner = owner;
  }

  @Override
  public String getNote() {
    return note;
  }

  @Override
  public void setNote(String note) {
    this.note = note;
  }

  @Override
  public String[] getTags() {
    if (tags == null) return new String[0];
    return tags;
  }

  @Override
  public void setTags(String[] tags) {
    this.tags = tags;
  }

}
