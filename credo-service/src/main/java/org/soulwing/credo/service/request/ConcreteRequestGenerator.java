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
package org.soulwing.credo.service.request;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.SigningRequest;
import org.soulwing.credo.SigningRequestBuilderFactory;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.SigningRequestEditor;
import org.soulwing.credo.service.SigningRequestException;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.CertificationRequestBuilder;
import org.soulwing.credo.service.crypto.CertificationRequestException;
import org.soulwing.credo.service.crypto.CertificationRequestWrapper;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.protect.SigningRequestProtectionService;

/**
 * A concrete {@link SigningRequestGenerator} implementation.
 * 
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteRequestGenerator
    implements SigningRequestGenerator {

  @Inject
  protected KeyGeneratorService keyGeneratorService;

  @Inject
  protected CertificationRequestBuilder csrBuilder;

  @Inject
  protected SigningRequestBuilderFactory requestBuilderFactory;

  @Inject
  protected SigningRequestProtectionService protectionService;

  @Override
  public SigningRequest generate(SigningRequestEditor editor,
      ProtectionParameters protection, Errors errors)
      throws NoSuchGroupException, GroupAccessException, UserAccessException,
      SigningRequestException {
    
    KeyPairWrapper keyPair = keyGeneratorService.generateKeyPair();
    
    try {
      
      CertificationRequestWrapper csr =
          csrBuilder.setPublicKey(keyPair.getPublic())
              .setSubject(editor.getSubject()).build();
      
      SigningRequest request = requestBuilderFactory.newBuilder()
          .setSubject(csr.getSubject())
          .setCertificationRequest(csr.getContent())
          .build();
      
      protectionService.protect(request, keyPair.getPrivate(), protection);
      return request;
    }
    catch (CertificationRequestException ex) {
      throw new SigningRequestException();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
