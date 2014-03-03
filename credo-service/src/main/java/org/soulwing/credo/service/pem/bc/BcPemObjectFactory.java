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
import java.io.StringReader;

import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PemObjectFactory} implemented using Bouncy Castle.
 *
 * @author Carl Harris
 */
public class BcPemObjectFactory implements PemObjectFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public PemObjectWrapper newPemObject(String encoded) {
    try (PemReader reader = new PemReader(new StringReader(encoded))) {
      PemObject obj = reader.readPemObject();
      if (obj == null) {
        throw new IllegalArgumentException("not a PEM object");
      }
      return new BcPemObjectWrapper(obj);
    }
    catch (PEMException ex) {
      throw new IllegalArgumentException("illegal PEM object", ex);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
