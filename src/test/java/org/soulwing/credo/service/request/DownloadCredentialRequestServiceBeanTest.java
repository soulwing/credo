/*
 * File created on Apr 12, 2014 
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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.credential.NoSuchCredentialException;

/**
 * Unit tests for {@link DownloadCredentialRequestServiceBean}.
 *
 * @author Carl Harris
 */
public class DownloadCredentialRequestServiceBeanTest {

  private static final long REQUEST_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private CredentialRequestRepository requestRepository;

  @Mock
  private CreateCredentialRequestService createRequestService;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private FileDownloadResponse response;
  

  private DownloadCredentialRequestServiceBean service = new DownloadCredentialRequestServiceBean();
  
  @Before
  public void setUp() throws Exception {
    service.requestRepository = requestRepository;
    service.createRequestService = createRequestService;
  }

  @Test
  public void testDownloadRequestById() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(createRequestService).downloadRequest(with(same(request)), 
          with(same(response)));
    } });    
    
    service.downloadRequest(REQUEST_ID, response);
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testDownloadRequestByIdWhenNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(null));
    } });    
    
    service.downloadRequest(REQUEST_ID, response);
  }

}
