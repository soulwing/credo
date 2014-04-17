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
package org.soulwing.credo.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;

import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link TimerServiceManagerBean}.
 *
 * @author Carl Harris
 */
public class TimerServiceManagerBeanTest {

  private static final Long DURATION = -1L;
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private TimerService timerService;
  
  @Mock
  private Timer timer;
  
  @Mock
  private TimerDelegate delegate;
  
  private TimerServiceManagerBean bean = new TimerServiceManagerBean();
  
  @Before
  public void setUp() throws Exception {
    bean.timerService = timerService;
  }
  
  @Test
  public void testCreateTimer() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(timerService).createTimer(with(DURATION), with(same(delegate)));
      will(returnValue(timer));
    } });
    
    assertThat(bean.createTimer(DURATION, delegate), is(sameInstance(timer)));
  }
  
  @Test
  public void testTimeout() throws Exception {
    context.checking(new Expectations() { {
      oneOf(timer).getInfo();
      will(returnValue(delegate));
      oneOf(delegate).timeout(with(same(timer)));
    } });
    
    bean.timeout(timer);
  }
  
  @Test
  public void testCancel() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(timerService).getTimers();
      will(returnValue(Collections.singleton(timer)));
      oneOf(timer).getInfo();
      will(returnValue(delegate));
      oneOf(timer).cancel();
    } });
    
    assertThat(bean.cancel(delegate), is(true));
  }

  @Test
  public void testCancelWhenNotFound() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(timerService).getTimers();
      will(returnValue(Collections.emptySet()));
    } });
    
    assertThat(bean.cancel(delegate), is(false));
  }

  @Test
  public void testCancelWhenAlreadyCanceled() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(timerService).getTimers();
      will(returnValue(Collections.singleton(timer)));
      oneOf(timer).getInfo();
      will(returnValue(delegate));
      oneOf(timer).cancel();
      will(throwException(new NoSuchObjectLocalException()));
    } });
    
    assertThat(bean.cancel(delegate), is(false));
  }


}
