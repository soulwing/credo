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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.soulwing.credo.service.pem.PemObjectBuilder;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PemObjectBuilder} implemented using Bouncy Castle.
 *
 * @author Carl Harris
 */
public class BcPemObjectBuilder implements PemObjectBuilder {

  private final List<PemHeader> headers = new ArrayList<>();
  private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  
  private String type;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PemObjectBuilder setType(String type) {
    this.type = type;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PemObjectBuilder setHeader(String name, Object value) {
    headers.add(new PemHeader(name, value.toString()));
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PemObjectBuilder append(byte[] data) {
    try {
      buffer.write(data);
      return this;
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PemObjectWrapper build() {
    if (type == null) {
      throw new IllegalStateException("type is required");
    }
    byte[] data = buffer.toByteArray();
    if (data.length == 0) {
      throw new IllegalStateException("no data");
    }
    return new BcPemObjectWrapper(new PemObject(type, headers, data));
  }

}
