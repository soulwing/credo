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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Test;
import org.soulwing.credo.service.pem.PemHeaderWrapper;

/**
 * Tests for {@link BcPemObjectWrapper}.
 *
 * @author Carl Harris
 */
public class BcPemObjectWrapperTest {

  private final String type = "SOME TYPE";
  private final byte[] content = { 0, 1, 2, 3 };
  private final List<PemHeader> headers = new ArrayList<>();
  
  private final PemObject obj = new PemObject(type, headers, content);
  private final BcPemObjectWrapper wrapper = new BcPemObjectWrapper(obj);
  
  @Test
  public void testGetType() throws Exception {
    assertThat(wrapper.getType(), is(equalTo(type)));
  }
  
  @Test
  public void testGetHeader() throws Exception {
    headers.add(new PemHeader("Some-Header", "Some-Value"));
    PemHeaderWrapper header = wrapper.getHeader("Some-Header");
    assertThat(header, is(not(nullValue())));
    assertThat(header.getName(), is(equalTo("Some-Header")));
    assertThat(header.getStringValue(), is(equalTo("Some-Value")));
  }
  
  @Test
  public void testGetHeaderNotFound() throws Exception {
    assertThat(headers.isEmpty(), is(true));
    assertThat(wrapper.getHeader("Some-Header"), is(nullValue()));
  }

  @Test
  public void testGetContentLength() throws Exception {
    assertThat(wrapper.getContentLength(), is(equalTo(content.length)));
  }
  
  @Test
  public void testGetContent() throws Exception {
    byte[] actual = wrapper.getContent();
    assertThat(actual, is(equalTo(content)));
    assertThat(wrapper.getContent(), is(sameInstance(actual)));
  }
  
  @Test
  public void testWriteContent() throws Exception {
    StringWriter writer = new StringWriter();
    wrapper.writeContent(writer);
    PemReader reader = new PemReader(new StringReader(writer.toString()));
    PemObject obj = reader.readPemObject();
    assertThat(obj, is(not(nullValue())));
    assertThat(reader.readPemObject(), is(nullValue()));
    reader.close();
    assertThat(obj.getType(), is(equalTo(type)));
    assertThat(obj.getContent(), is(equalTo(content)));
  }
}
