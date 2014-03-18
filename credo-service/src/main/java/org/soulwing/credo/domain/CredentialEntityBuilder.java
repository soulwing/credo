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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.soulwing.credo.Credential;
import org.soulwing.credo.CredentialBuilder;
import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.Tag;

/**
 * A {@link CredentialBuilder} that builds a {@link CredentialEntity}.
 *
 * @author Carl Harris
 */
public class CredentialEntityBuilder implements CredentialBuilder {

  private final CredentialEntity credential = new CredentialEntity();
  
  @Override
  public CredentialBuilder setName(String name) {
    credential.setName(name);
    return this;
  }

  @Override
  public CredentialBuilder setNote(String note) {
    credential.setNote(note);
    return this;
  }

  @Override
  public CredentialBuilder setTags(Collection<Tag> tags) {
    Set<Tag> tagSet = new LinkedHashSet<>();
    tagSet.addAll(tags);
    credential.setTags(tagSet);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder setIssuer(String issuer) {
    credential.setIssuer(issuer);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder setExpiration(Date expiration) {
    credential.setExpiration(expiration);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder setPrivateKey(String content) {
    CredentialKeyEntity privateKey = new CredentialKeyEntity();
    privateKey.setEncoded(content);
    credential.setPrivateKey(privateKey);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialBuilder addCertificate(CredentialCertificate certificate) {
    assertIsCertificateEntity(certificate);
    credential.addCertificate(
        (CredentialCertificateEntity) certificate);
    return this;
  }

  private void assertIsCertificateEntity(CredentialCertificate certificate) {
    if (!(certificate instanceof CredentialCertificateEntity)) {
      throw new IllegalArgumentException("illegal certificate type: "
          + certificate.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential build() {
    return credential;
  }

}
