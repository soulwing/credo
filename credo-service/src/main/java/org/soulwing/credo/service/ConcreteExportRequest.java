/*
 * File created on Feb 24, 2014 
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

import org.soulwing.credo.Credential;

/**
 * A concrete {@link ExportRequest} implementation.
 *
 * @author Carl Harris
 */
public class ConcreteExportRequest 
    implements Serializable, ExportRequest {

  private static final long serialVersionUID = -195819883911230303L;

  private final Credential credential;
  
  private ExportFormat format = ExportFormat.PEM_ARCHIVE;
  
  /**
   * Constructs a new instance.
   * @param credential
   */
  public ConcreteExportRequest(Credential credential) {
    this.credential = credential;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential getCredential() {
    return credential;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPassphraseRequired() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getPassphrase() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassphrase(char[] passphrase) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFileName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFileName(String fileName) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getExportPassphrase() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setExportPassphrase(char[] exportPassphrase) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportFormat getFormat() {
    return format;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFormat(ExportFormat format) {
    this.format = format;
  }

}
