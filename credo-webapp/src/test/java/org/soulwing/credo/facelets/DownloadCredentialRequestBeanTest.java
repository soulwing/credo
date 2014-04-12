/*
 * File created on Mar 22, 2014 
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
package org.soulwing.credo.facelets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.faces.context.FacesContext;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.NoSuchCredentialException;
import org.soulwing.credo.service.request.DownloadRequestService;

/**
 * Unit tests for {@link DownloadCredentialRequestBean}.
 *
 * @author Carl Harris
 */
public class DownloadCredentialRequestBeanTest {

  private static final long REQUEST_ID = -1L;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() { { 
    setImposteriser(ClassImposteriser.INSTANCE);
  } };
  
  @Mock
  private DownloadRequestService requestService;
  
  @Mock
  private FacesContext facesContext;
  
  private DownloadCredentialRequestBean bean = 
      new DownloadCredentialRequestBean();
  
  @Before
  public void setUp() throws Exception {
    bean.requestService = requestService;
    bean.facesContext = facesContext;
  }
  
  @Test
  public void testDownload() throws Exception {
    context.checking(new Expectations() { {
      oneOf(requestService).downloadRequest(
          with(REQUEST_ID), 
          with(any(FileDownloadResponse.class)));
      oneOf(facesContext).responseComplete();
    } });

    bean.setId(REQUEST_ID);
    assertThat(bean.download(), 
        is(equalTo(DownloadCredentialRequestBean.SUCCESS_OUTCOME_ID)));
  }

  @Test
  public void testDownloadWhenNotFound() throws Exception {
    context.checking(new Expectations() { {
      oneOf(requestService).downloadRequest(
          with(REQUEST_ID), 
          with(any(FileDownloadResponse.class)));
      will(throwException(new NoSuchCredentialException()));
    } });

    bean.setId(REQUEST_ID);
    assertThat(bean.download(), 
        is(equalTo(DownloadCredentialRequestBean.SUCCESS_OUTCOME_ID)));
  }

}
