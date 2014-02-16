/*
 * File created on Feb 14, 2014 
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
import java.io.InputStream;

import javax.servlet.http.Part;

import org.soulwing.credo.service.FileContentModel;

/**
 * A {@link FileContentModel} that wraps a {@link Part}.
 *
 * @author Carl Harris
 */
public class PartContent implements FileContentModel {

  private final Part part;
  
  /**
   * Constructs a new instance.
   * @param part the part delegate
   */
  public PartContent(Part part) {
    this.part = part;
  }

  /**
   * Gets the {@link Part} delegate.
   * @return part delegate
   */
  public Part getPart() {
    return part;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return part.getSubmittedFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContentType() {
    return part.getContentType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getInputStream() throws IOException {
    return part.getInputStream();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return part.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof PartContent)) return false;
    return this.part == ((PartContent) obj).part;
  }

}
