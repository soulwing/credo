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
  
  private char[] passphrase;
  private char[] exportPassphrase;
  private String fileName;  
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
    return passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassphrase(char[] passphrase) {
    this.passphrase = passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFileName() {
    if (fileName != null) return fileName;
    return credential.getName().trim().replaceAll("\\.|\\s+", "_")
        + format.getFileSuffix();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getExportPassphrase() {
    return exportPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setExportPassphrase(char[] exportPassphrase) {
    this.exportPassphrase = exportPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportFormat getFormat() {
    if (format == null) return ExportFormat.PEM_ARCHIVE;
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
