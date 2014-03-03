/*
 * File created on Mar 2, 2014 
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
package org.soulwing.credo.service.crypto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Before;
import org.junit.Test;
import org.soulwing.credo.service.crypto.Crypt4jPasswordEncryptionService;

/**
 * Unit tests for {@link Crypt4jPasswordEncryptionService}.
 *
 * @author Carl Harris
 */
public class Crypt4jPasswordEncryptionServiceTest {

  private final Crypt4jPasswordEncryptionService service =
      new Crypt4jPasswordEncryptionService();
  
  @Before
  public void setUp() throws Exception {
    service.init();
  }
  
  @Test
  public void testEncryptAndValidate() throws Exception {
    char[] password = "secret".toCharArray();
    String encrypted = service.encrypt(password);
    assertThat(encrypted, 
        is(startsWith(Crypt4jPasswordEncryptionService.SALT_PREFIX)));    
    assertThat(service.validate(password, encrypted), is(true));
  }
  
}
