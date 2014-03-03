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
import static org.hamcrest.Matchers.nullValue;

import java.io.StringReader;

import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Test;
import org.soulwing.credo.service.pem.PemObjectWrapper;

/**
 * Tests for {@link BcPemObejctBuilder}.
 *
 * @author Carl Harris
 */
public class BcPemObjectBuilderTest {

  @Test
  public void testConfigureAndBuild() throws Exception {
    byte[] content = { 0, 1, 2, 3 };
    BcPemObjectBuilder builder = new BcPemObjectBuilder();
    builder.setType("SOME TYPE");
    builder.setHeader("Some-Header", "Some-Value");
    builder.append(content);
    PemObjectWrapper wrapper = builder.build();
    StringReader reader = new StringReader(wrapper.getEncoded());
    PemReader pemReader = new PemReader(reader);
    PemObject obj = pemReader.readPemObject();
    assertThat(pemReader.readPemObject(), is(nullValue()));
    pemReader.close();
    assertThat(obj.getHeaders().size(), is(equalTo(1)));
    PemHeader header = (PemHeader) obj.getHeaders().get(0);
    assertThat(header.getName(), is(equalTo("Some-Header")));
    assertThat(header.getValue(), is(equalTo("Some-Value")));
    assertThat(obj.getContent(), is(equalTo(content)));
  }
  
}
