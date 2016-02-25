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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import javax.security.auth.x500.X500Principal;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.CredentialRequestBuilder;
import org.soulwing.credo.CredentialRequestBuilderFactory;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.crypto.CertificationRequestBuilder;
import org.soulwing.credo.service.crypto.CertificationRequestBuilderFactory;
import org.soulwing.credo.service.crypto.CertificationRequestException;
import org.soulwing.credo.service.crypto.CertificationRequestWrapper;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.PublicKeyWrapper;
import org.soulwing.credo.service.group.GroupResolver;
import org.soulwing.credo.service.protect.CredentialRequestProtectionService;

/**
 * Unit tests for {@link CredentialRequestGeneratorBean}.
 *
 * @author Carl Harris
 */
public class CredentialRequestGeneratorBeanTest {

  private static final String GROUP_NAME = "someGroup";
  
  private static final String ENCODED_CSR = "encodedCertificationRequest";

  private static final X500Principal SUBJECT = new X500Principal("cn=Some Subject");

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private KeyGeneratorService keyGeneratorService;
  
  @Mock
  private CertificationRequestBuilderFactory csrBuilderFactory;
  
  @Mock
  private CredentialRequestBuilderFactory requestBuilderFactory;
  
  @Mock
  private CredentialRequestProtectionService protectionService;
  
  @Mock
  private CredentialRequestEditor editor;
  
  @Mock
  private CertificationRequestBuilder csrBuilder;
  
  @Mock
  private CredentialRequestBuilder requestBuilder;
  
  @Mock
  private GroupResolver groupResolver;
  
  @Mock
  private CredentialRequest request;

  @Mock
  private ProtectionParameters protection;

  @Mock
  private KeyPairWrapper keyPair;
  
  @Mock
  private PublicKeyWrapper publicKey;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  @Mock
  private CertificationRequestWrapper csr;
  
  @Mock
  private UserGroup group;
  
  @Mock
  private Errors errors;
  
  private CredentialRequestGeneratorBean generator =
      new CredentialRequestGeneratorBean();
  
  @Before
  public void setUp() throws Exception {
    generator.keyGeneratorService = keyGeneratorService;
    generator.csrBuilderFactory = csrBuilderFactory;
    generator.groupResolver = groupResolver;
    generator.requestBuilderFactory = requestBuilderFactory;
    generator.protectionService = protectionService;
  }
  
  @Test
  public void testGenerateSuccess() throws Exception {
    context.checking(keyPairExpectations());
    context.checking(csrBuilderExpectations(returnValue(csr)));
    context.checking(signingRequestExpectations());
    context.checking(protectionServiceExpectations(returnValue(null)));    
    assertThat(generator.generate(editor, protection, errors),
        is(sameInstance(request)));
  }

  @Test(expected = CredentialRequestException.class)
  public void testGenerateWhenCertificationRequestException() throws Exception {
    context.checking(keyPairExpectations());
    context.checking(csrBuilderExpectations(
        throwException(new CertificationRequestException("some message"))));

    generator.generate(editor, protection, errors);
  }
  
  private Expectations keyPairExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(keyGeneratorService).generateKeyPair();
      will(returnValue(keyPair));
      allowing(keyPair).getPublic();
      will(returnValue(publicKey));
      allowing(keyPair).getPrivate();
      will(returnValue(privateKey));
    } };
  }
  
  private Expectations csrBuilderExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(editor).getSubject();
      will(returnValue(SUBJECT));
      oneOf(csrBuilderFactory).newBuilder();
      will(returnValue(csrBuilder));
      oneOf(csrBuilder).setSubject(with(SUBJECT));
      will(returnValue(csrBuilder));
      oneOf(csrBuilder).setPublicKey(with(same(publicKey)));
      will(returnValue(csrBuilder));
      oneOf(csrBuilder).build(with(same(privateKey)));
      will(outcome);
    } };
  }
  
  private Expectations signingRequestExpectations() throws Exception {
    return new Expectations() { { 
      oneOf(csr).getSubject();
      will(returnValue(SUBJECT));
      oneOf(csr).getContent();
      will(returnValue(ENCODED_CSR));
      oneOf(requestBuilderFactory).newBuilder();
      will(returnValue(requestBuilder));
      oneOf(requestBuilder).setSubject(with(SUBJECT));
      will(returnValue(requestBuilder));
      oneOf(requestBuilder).setCertificationRequest(with(ENCODED_CSR));
      will(returnValue(requestBuilder));
      oneOf(requestBuilder).build();
      will(returnValue(request));
    } };
  }

  private Expectations protectionServiceExpectations(
      final Action outcome) throws Exception {
    return new Expectations() { { 
      oneOf(protection).getGroupName();
      will(returnValue(GROUP_NAME));
      oneOf(groupResolver).resolveGroup(with(GROUP_NAME), with(same(errors)));
      will(returnValue(group));
      oneOf(request).setOwner(group);
      oneOf(protectionService).protect(
          with(same(request)), 
          with(same(privateKey)), 
          with(same(protection)));
      will(outcome);
    } };
  }
}
