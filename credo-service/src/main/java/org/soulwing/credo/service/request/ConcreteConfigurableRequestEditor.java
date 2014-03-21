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
package org.soulwing.credo.service.request;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.security.auth.x500.X500Principal;

import org.soulwing.credo.Tag;
import org.soulwing.credo.service.X500PrincipalUtil;

/**
 * A concrete {@link ConfigurableRequestEditor} implementation.
 *
 * @author Carl Harris
 */
@Dependent
public class ConcreteConfigurableRequestEditor
    implements ConfigurableRequestEditor {

  private String name;
  private String subjectName;
  private String owner;
  private String note;
  private String[] tags;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getSubject() {
    if (subjectName == null) return null;
    return new X500Principal(subjectName);
  }

  @Override
  public String getSubjectName() {
    return subjectName;
  }

  @Override
  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
    this.name = X500PrincipalUtil.getCommonName(subjectName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubjectCommonName() {
    return X500PrincipalUtil.getCommonName(subjectName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuerCommonName() {
    // a signing request does not have an issuer
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getExpiration() {
    // a signing request does not have an expiration date
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOwner() {
    return owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getNote() {
    return note;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getTags() {
    if (tags == null) return new String[0];
    return tags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTags(String[] tags) {
    this.tags = tags;
  }

  @Override
  public void setTags(Set<? extends Tag> tagSet) {
    String[] tags = new String[tagSet.size()];
    int index = 0;
    Iterator<? extends Tag> i = tagSet.iterator();
    while (i.hasNext()) {
      tags[index++] = i.next().getText();
    }
    setTags(tags);
  }

}
