/*
 * File created on Feb 19, 2014 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public class BcPemUtil {

  public static List<Object> readAllObjects(InputStream inputStream)
      throws IOException {
    List<Object> objects = new ArrayList<>();
    try (PEMParser parser = new PEMParser(
        new InputStreamReader(inputStream, "UTF-8"))) {
      Object obj = parser.readObject();
      while (obj != null) {
        objects.add(obj);
        obj = parser.readObject();
      }
      return objects;
    }
  }
  
  public static void writePrivateKey(Object privateKey, char[] passphrase,
      Writer writer) throws IOException {
    try (PEMWriter pemWriter = new PEMWriter(writer)) {
      PrivateKeyInfo privateKeyInfo = BcPemUtil.extractPrivateKeyInfo(
          privateKey, passphrase);
      if (passphrase == null) {
        pemWriter.writeObject(privateKeyInfo);
      }
      else {
        PKCS8Generator generator = new PKCS8Generator(privateKeyInfo, 
            createPrivateKeyEncryptor(passphrase));
        pemWriter.writeObject(generator.generate());
      }
      pemWriter.flush();
    }
  }
  
  public static void writeCertificate(X509CertificateHolder certificate,
      Writer writer) throws IOException {
    try (PEMWriter pemWriter = new PEMWriter(writer)) {
      pemWriter.writeObject(certificate);
      pemWriter.flush();
    }
  }
  
  public static PrivateKeyInfo extractPrivateKeyInfo(Object key,
      char[] passphrase) throws IllegalArgumentException {
    if (key instanceof PKCS8EncryptedPrivateKeyInfo) {
      return decryptPrivateKey(passphrase, (PKCS8EncryptedPrivateKeyInfo) key);
    }
    else if (key instanceof PEMKeyPair) {
      return ((PEMKeyPair) key).getPrivateKeyInfo();
    }
    else {
      throw new RuntimeException("unexpected key type " 
            + key.getClass().getName());
    }
  }

  public static AsymmetricKeyParameter extractPrivateKey(
      PrivateKeyInfo privateKeyInfo) {
    try {      
      return PrivateKeyFactory.createKey(
          privateKeyInfo);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static PrivateKeyInfo decryptPrivateKey(char[] passphrase,
      PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) 
      throws IllegalArgumentException {
    try {
      return encryptedPrivateKeyInfo.decryptPrivateKeyInfo(
          createPrivateKeyDecryptor(passphrase));
    }
    catch (PKCSException ex) {
      throw new IllegalArgumentException("incorrect passphrase");
    }
  }

  private static InputDecryptorProvider createPrivateKeyDecryptor(
      char[] passphrase) {
    try {
      Validate.notNull(passphrase, "passphrase is required");
      return new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase);
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static OutputEncryptor createPrivateKeyEncryptor(char[] passphrase) {
    try {
      Validate.notNull(passphrase, "passphrase is required");
      return new JceOpenSSLPKCS8EncryptorBuilder(
          PKCS8Generator.PBE_SHA1_3DES)
          .setPasssword(passphrase)
          .setIterationCount(100)
          .build();
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }


  public static AsymmetricKeyParameter createPublicKey(
      SubjectPublicKeyInfo publicKeyInfo) {
    try {
      return PublicKeyFactory.createKey(publicKeyInfo);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
