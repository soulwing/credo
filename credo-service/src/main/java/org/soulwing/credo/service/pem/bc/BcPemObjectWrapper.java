/*
 * File created on Feb 28, 2014 
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
package org.soulwing.credo.service.pem.bc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.soulwing.credo.service.pem.PemHeaderWrapper;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PemObjectWrapper} implemented using Bouncy Castle.
 *
 * @author Carl Harris
 */
public class BcPemObjectWrapper implements PemObjectWrapper {

  private final PemObject delegate;
  private byte[] content;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public BcPemObjectWrapper(PemObject delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType() {
    return delegate.getType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PemHeaderWrapper getHeader(String name) {
    Iterator i = delegate.getHeaders().iterator();
    while (i.hasNext()) {
      PemHeader header = (PemHeader) i.next();
      if (header.getName().equals(name)) {
        return new BcPemHeaderWrapper(header);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getContentLength() {
    return getContent().length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getContent() {
    if (content == null) {
      content = delegate.getContent();
    }
    return content;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getEncoded() {
    StringWriter writer = new StringWriter();
    try {
      writeContent(writer);
      return writer.toString();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeContent(Writer writer) throws IOException {
    try (PemWriter pemWriter = new PemWriter(writer)) {
      pemWriter.writeObject(delegate);
    }
  }

}
