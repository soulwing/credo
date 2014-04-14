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

import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.CredentialRequestBuilderFactory;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.crypto.CertificationRequestBuilderFactory;
import org.soulwing.credo.service.crypto.CertificationRequestException;
import org.soulwing.credo.service.crypto.CertificationRequestWrapper;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.group.GroupResolver;
import org.soulwing.credo.service.protect.CredentialRequestProtectionService;

/**
 * A concrete {@link CredentialRequestGenerator} implementation.
 * 
 * @author Carl Harris
 */
@ApplicationScoped
public class CredentialRequestGeneratorBean
    implements CredentialRequestGenerator {

  @Inject
  protected KeyGeneratorService keyGeneratorService;

  @Inject
  protected CertificationRequestBuilderFactory csrBuilderFactory;

  @Inject
  protected CredentialRequestBuilderFactory requestBuilderFactory;

  @Inject
  protected GroupResolver groupResolver;
  
  @Inject
  protected CredentialRequestProtectionService protectionService;

  @Override
  public CredentialRequest generate(CredentialRequestEditor editor,
      ProtectionParameters protection, Errors errors)
      throws NoSuchGroupException, GroupAccessException, UserAccessException,
      CredentialRequestException {
    
    KeyPairWrapper keyPair = keyGeneratorService.generateKeyPair();
    try {
      
      CertificationRequestWrapper csr = csrBuilderFactory.newBuilder()
          .setPublicKey(keyPair.getPublic())
          .setSubject(editor.getSubject())
          .build(keyPair.getPrivate());
      
      CredentialRequest request = requestBuilderFactory.newBuilder()
          .setSubject(csr.getSubject())
          .setCertificationRequest(csr.getContent())
          .build();
      
      request.setOwner(groupResolver.resolveGroup(
          protection.getGroupName(), errors));
      protectionService.protect(request, keyPair.getPrivate(), protection);
      return request;
    }
    catch (CertificationRequestException ex) {
      throw new CredentialRequestException();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
