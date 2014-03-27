/*
 * File created on Mar 27, 2014 
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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemHeaderWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@linK JcaSecretKeyDecoderStrategy} that decodes AES-encrypted secret
 * keys.
 *
 * @author Carl Harris
 */
@Dependent
public class JcaAESSecretKeyDecoderStrategy
    implements JcaSecretKeyDecoderStrategy {

  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKeyWrapper decode(PemObjectWrapper object) {
    PemHeaderWrapper header = object.getHeader("DEK-Info");
    Validate.notNull(header, "no DEK-Info header");
    String value = header.getStringValue();
    int index = value.indexOf(',');
    String transform = value.substring(0, index);
    if (!transform.startsWith("AES/")) return null;
    byte[] iv = decodeIV(value, index + 1);
    byte[] cipherText = object.getContent();
    return new JcaAESEncryptedSecretKeyWrapper(transform, iv, cipherText, 
        objectBuilderFactory);
  }

  private byte[] decodeIV(String value, int index) {
    try {
      return Hex.decodeHex(value.substring(index).toCharArray());
    }
    catch (DecoderException ex) {
      throw new IllegalArgumentException("invalid IV", ex);
    }
  }

}
