/*
 * File created on Mar 3, 2014 
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
package org.soulwing.credo.service.crypto.jca;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.crypto.Encoded;
import org.soulwing.credo.service.crypto.Encoded.Type;
import org.soulwing.credo.service.crypto.PrivateKeyDecoder;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemHeaderWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link PrivateKeyDecoder} that decodes an PEM-encoded AES-wrapped 
 * private key.
 *
 * @author Carl Harris
 */
@Encoded(Type.AES)
@ApplicationScoped
public class JcaAESPrivateKeyDecoder implements PrivateKeyDecoder {

  @Inject
  protected PemObjectFactory objectFactory;
  
  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKeyWrapper decode(String encoded) {
    PemObjectWrapper object = objectFactory.newPemObject(encoded);
    PemHeaderWrapper header = object.getHeader("DEK-Info");
    Validate.notNull(header, "no DEK-Info header");
    String value = header.getStringValue();
    int index = value.indexOf(',');
    String transform = value.substring(0, index);
    byte[] iv = decodeIV(value, index);
    byte[] cipherText = object.getContent();
    return new JcaEncryptedPrivateKeyWrapper(transform, iv, cipherText, 
        objectBuilderFactory);
  }

  private byte[] decodeIV(String value, int index) {
    try {
      return Hex.decodeHex(value.substring(index + 1).toCharArray());
    }
    catch (DecoderException ex) {
      throw new IllegalArgumentException("invalid IV", ex);
    }
  }

}
