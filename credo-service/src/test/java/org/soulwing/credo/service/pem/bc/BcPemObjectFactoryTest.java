/*
 * File created on Mar 3, 2014 
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
package org.soulwing.credo.service.pem.bc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Test;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * Tests for {@link BcPemObjectFactory}.
 *
 * @author Carl Harris
 */
public class BcPemObjectFactoryTest {

  @Test
  public void testNewPemObject() throws Exception {
    byte[] content = { 0, 1, 2, 3 };
    List headers = Arrays.asList(new PemHeader("Some-Header", "Some-Value"));
    PemObject obj = new PemObject("SOME TYPE", headers, content);
    StringWriter writer = new StringWriter();
    PemWriter pemWriter = new PemWriter(writer);
    pemWriter.writeObject(obj);
    pemWriter.close();
    BcPemObjectFactory factory = new BcPemObjectFactory();
    PemObjectWrapper wrapper = factory.newPemObject(writer.toString());
    assertThat(wrapper.getType(), is(equalTo("SOME TYPE")));
    assertThat(wrapper.getHeader("Some-Header").getStringValue(), 
        is(equalTo("Some-Value")));
    assertThat(wrapper.getContent(), is(equalTo(content)));
  }
  
}
