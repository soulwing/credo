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
import org.soulwing.credo.Password;

/**
 * A concrete {@link ExportRequest} implementation.
 *
 * @author Carl Harris
 */
public class ConcreteExportRequest 
    implements Serializable, ExportRequest {

  private static final long serialVersionUID = -195819883911230303L;

  private final Credential credential;
  
  private Password exportPassphrase;
  private String fileName;  
  private String format;
  private ProtectionParameters protectionParameters;
  
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
  public String getFileName() {
    if (fileName != null) return fileName;
    return credential.getName().trim().replaceAll("\\.|\\s+", "_");
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
  public String getSuffixedFileName(String suffix) {
    if (fileName == null) {
      throw new IllegalStateException("no file name set");
    }
    if (fileName.indexOf('.') != -1) {
      return fileName;
    }
    return fileName + suffix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Password getExportPassphrase() {
    return exportPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setExportPassphrase(Password exportPassphrase) {
    this.exportPassphrase = exportPassphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFormat() {    
    return format;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFormat(String format) {
    this.format = format;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProtectionParameters getProtectionParameters() {
    return protectionParameters;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProtectionParameters(ProtectionParameters parameters) {
    this.protectionParameters = parameters;
  }
  
}
