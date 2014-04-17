/*
 * File created on Apr 17, 2014 
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
package org.soulwing.credo.service.protect;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.security.PrivateKey;

import javax.ejb.Timer;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.TimerServiceManager;

/**
 * Unit tests for {@link PrivateKeyHolderBean}.
 *
 * @author Carl Harris
 */
public class PrivateKeyHolderBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private TimerServiceManager timerServiceManager;
  
  @Mock
  private Timer timer;
  
  @Mock
  private PrivateKey privateKey;
  
  private PrivateKeyHolderBean bean = new PrivateKeyHolderBean();
  
  @Before
  public void setUp() throws Exception {
    bean.timerServiceManager = timerServiceManager;
  }
  
  @Test
  public void testGetAndSetPrivateKey() throws Exception {
    context.checking(new Expectations() { { 
      // each call to getPrivateKey or setPrivateKey when key is not null
      // should schedule the timer
      exactly(2).of(timerServiceManager).createTimer(
          with(PrivateKeyHolderBean.TIMEOUT_DURATION),
          with(same(bean)));
      will(returnValue(timer));
      // if the timer is rescheduled when already set, the existing timer
      // must be canceled
      atLeast(1).of(timerServiceManager).cancel(with(same(bean)));
    } });
    
    bean.setPrivateKey(privateKey);
    assertThat(bean.getPrivateKey(), is(sameInstance(privateKey)));
  }
  
  @Test
  public void testSetPrivateKeyToNull() throws Exception {
    context.checking(new Expectations() { {
      // setting key to non-null must schedule the timer
      oneOf(timerServiceManager).createTimer(
          with(PrivateKeyHolderBean.TIMEOUT_DURATION),
          with(same(bean)));
      will(returnValue(timer));      
      // setting key to null must cancel the timer and not reschedule it
      atLeast(1).of(timerServiceManager).cancel(with(same(bean)));
    } });
    
    bean.setPrivateKey(privateKey);
    bean.setPrivateKey(null);
    assertThat(bean.getPrivateKey(), is(nullValue()));
  }

  @Test
  public void testGetPrivateKeyWhenNull() throws Exception {
    context.checking(new Expectations() { {
      allowing(timerServiceManager).cancel(with(same(bean)));
    } });
    assertThat(bean.getPrivateKey(), is(nullValue()));
  }

}
