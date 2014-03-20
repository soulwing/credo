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
package org.soulwing.credo.facelets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.FacesContext;

import org.soulwing.credo.service.FileDownloadResponse;

/**
 * A {@link FileDownloadResponse} that delegates to a {@link FacesContext}.
 *
 * @author Carl Harris
 */
public class FacesFileDownloadResponse implements FileDownloadResponse {

  static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
  
  static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
  
  private final FacesContext facesContext;
    
  /**
   * Constructs a new instance.
   * @param facesContext
   */
  public FacesFileDownloadResponse(FacesContext facesContext) {
    this.facesContext = facesContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setContentType(String contentType) {
    facesContext.getExternalContext().setResponseContentType(contentType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCharacterEncoding(String encoding) {
    facesContext.getExternalContext().setResponseCharacterEncoding(encoding);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFileName(String fileName) {
    facesContext.getExternalContext().setResponseHeader(
        CONTENT_DISPOSITION_HEADER, 
        String.format(CONTENT_DISPOSITION_FORMAT, fileName));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Writer getWriter() throws IOException {
    return facesContext.getResponseWriter();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OutputStream getOutputStream() throws IOException {
    return facesContext.getResponseStream();
  }

}
