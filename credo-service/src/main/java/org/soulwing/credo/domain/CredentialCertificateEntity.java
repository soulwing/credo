/*
 * File created on Feb 18, 2014 
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
package org.soulwing.credo.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.soulwing.credo.CredentialCertificate;

/**
 * A {@link CredentialCertificate} implemented as a JPA entity.
 * 
 * @author Carl Harris
 */
@Entity
@Table(name = "credential_certificate")
public class CredentialCertificateEntity extends CredentialComponentEntity
    implements CredentialCertificate {

  private static final long serialVersionUID = -8685213395580438912L;
  
  @Column(nullable = false)
  private String subject;
  
  @Column(nullable = false)
  private String issuer;
  
  @Column(name = "serial_number", nullable = false)
  private String serialNumber;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "not_before", nullable = false)
  private Date notBefore;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "not_after", nullable = false)
  private Date notAfter;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the receiver's subject name.
   * @param subject the subject name to set
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuer() {
    return issuer;
  }

  /**
   * Sets the receiver's issuer name.
   * @param issuer the issuer name to set
   */
  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSerialNumber() {
    return serialNumber;
  }

  /**
   * Sets the receiver's serial number
   * @param serialNumber the serial number to set
   */
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotBefore() {
    return notBefore;
  }

  /**
   * Sets the receiver's not-before validity date.
   * @param notBefore the date to set
   */
  public void setNotBefore(Date notBefore) {
    this.notBefore = notBefore;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getNotAfter() {
    return notAfter;
  }

  /**
   * Sets the receiver's not-after validity date.
   * @param notBefore the date to set
   */
  public void setNotAfter(Date notAfter) {
    this.notAfter = notAfter;
  }

}
