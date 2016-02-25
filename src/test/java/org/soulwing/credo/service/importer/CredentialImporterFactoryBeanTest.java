/*
 * File created on Apr 13, 2014 
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
package org.soulwing.credo.service.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import javax.enterprise.inject.Instance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;

/**
 * Unit tests for {@link CredentialImporterFactoryBean}.
 *
 * @author Carl Harris
 */
public class CredentialImporterFactoryBeanTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Instance<ConfigurableCredentialImporter> importers;
  
  @Mock
  private ConfigurableCredentialImporter importer;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  private CredentialImporterFactoryBean bean = 
      new CredentialImporterFactoryBean();
  
  @Before
  public void setUp() throws Exception {
    bean.importers = importers;
  }
  
  @Test
  public void testNewImporter() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importers).get();
      will(returnValue(importer));
    } });
    
    assertThat(bean.newImporter(), is(sameInstance((Object) importer)));
  }

  @Test
  public void testNewImporterForExistingPrivateKey() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(importers).get();
      will(returnValue(importer));
      oneOf(importer).setPrivateKey(with(same(privateKey)));
    } });
    
    assertThat(bean.newImporter(privateKey), 
        is(sameInstance((Object) importer)));
  }

}
