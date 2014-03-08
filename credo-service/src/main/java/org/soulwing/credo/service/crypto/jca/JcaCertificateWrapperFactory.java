/*
 * File created on Mar 8, 2014 
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.CredentialCertificate;
import org.soulwing.credo.service.crypto.CertificateWrapper;
import org.soulwing.credo.service.crypto.CertificateWrapperFactory;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;
import org.soulwing.credo.service.pem.PemObjectFactory;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * A {@link CertificateWrapperFactory} that produces 
 * {@link JcaX509CertificateWrapper} objects.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaCertificateWrapperFactory
    implements CertificateWrapperFactory {

  @Inject
  protected PemObjectFactory objectFactory;
  
  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;

  protected CertificateFactory certificateFactory;
  
  /**
   * Initializes the receiver.
   */
  @PostConstruct
  public void init() {
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CertificateWrapper newCertificateWrapper(
      CredentialCertificate certificate) {
    PemObjectWrapper pemObject = 
        objectFactory.newPemObject(certificate.getContent());
    return new JcaX509CertificateWrapper(
        deriveCertificate(pemObject.getContent()), objectBuilderFactory);
  }

  /**
   * Derives a JCA {@link X509Certificate} from its DER encoded content.
   * @param encodedCertificate DER encoded certificate content
   * @return certificate
   */
  private X509Certificate deriveCertificate(byte[] encodedCertificate) {
    try (ByteArrayInputStream inputStream = 
        new ByteArrayInputStream(encodedCertificate)) {
      return (X509Certificate) 
          certificateFactory.generateCertificate(inputStream);
    }
    catch (CertificateException ex) {
      throw new RuntimeException(ex);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
