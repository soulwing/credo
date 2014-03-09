/*
 * File created on Feb 26, 2014 
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
package org.soulwing.credo.service.archive;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.Test;

/**
 * Tests for {@link ZipArchiveBuilder}.
 *
 * @author Carl Harris
 */
public class TarGzipArchiveBuilderTest {

  private TarGzipArchiveBuilder builder = new TarGzipArchiveBuilder();
  
  @Test(expected = IllegalStateException.class)
  public void testAddContentWithoutBeginEntry() throws Exception {
    builder.addContent(new StringReader("content"));
  }
  
  @Test(expected = IllegalStateException.class)
  public void testEndEntryWithoutBeginEntry() throws Exception {
    builder.endEntry();
  }

  @Test(expected = IllegalStateException.class)
  public void testBeginEntryWithoutEndEntry() throws Exception {
    builder.beginEntry("name", "charset");
    builder.beginEntry("name", "charset");
  }

  @Test(expected = IllegalStateException.class)
  public void testBuildWithoutEndEntry() throws Exception {
    builder.beginEntry("name", "charset");
    builder.build();
  }

  @Test
  public void testBuildArchiveWithSingleEntry() throws Exception {
    byte[] archive = builder.beginEntry("file.txt", "UTF-8")
        .addContent(new StringReader("content"))
        .endEntry().build();
    TarArchiveInputStream tis = new TarArchiveInputStream(
        new GzipCompressorInputStream(new ByteArrayInputStream(archive)));
    TarArchiveEntry entry = tis.getNextTarEntry();
    assertThat(entry.getMode(), is(equalTo(0100400)));
    assertThat(entry.getName(), is(equalTo("file.txt")));
    assertThat(readContent(tis), is(equalTo("content")));
    assertThat(tis.getNextEntry(), is(nullValue()));
  }

  @Test
  public void testBuildArchiveWithMultipleEntries() throws Exception {
    byte[] archive = builder
        .beginEntry("file1.txt", "UTF-8")
        .addContent(new StringReader("content1"))
        .endEntry()
        .beginEntry("file2.txt", "UTF-8")
        .addContent(new StringReader("content2"))
        .endEntry()
        .build();
    TarArchiveInputStream tis = new TarArchiveInputStream(
        new GzipCompressorInputStream(new ByteArrayInputStream(archive)));
    TarArchiveEntry entry = tis.getNextTarEntry();
    assertThat(entry.getName(), is(equalTo("file1.txt")));
    assertThat(readContent(tis), is(equalTo("content1")));
    entry = tis.getNextTarEntry();
    assertThat(entry.getName(), is(equalTo("file2.txt")));
    assertThat(readContent(tis), is(equalTo("content2")));
    assertThat(tis.getNextEntry(), is(nullValue()));
  }

  @Test
  public void testBuildArchiveEntryWithMultipleContent() throws Exception {
    byte[] archive = builder
        .beginEntry("file.txt", "UTF-8")
        .addContent(new StringReader("content1"))
        .addContent(new StringReader("content2"))
        .endEntry()
        .build();
    TarArchiveInputStream tis = new TarArchiveInputStream(
        new GzipCompressorInputStream(new ByteArrayInputStream(archive)));
    TarArchiveEntry entry = tis.getNextTarEntry();
    assertThat(entry.getName(), is(equalTo("file.txt")));
    assertThat(readContent(tis), is(equalTo("content1content2")));
    assertThat(tis.getNextEntry(), is(nullValue()));
  }

  private String readContent(TarArchiveInputStream zis)
      throws UnsupportedEncodingException, IOException {
    Reader reader = new InputStreamReader(zis, "UTF-8");
    char[] buf = new char[8192];
    int numRead = reader.read(buf);
    return new String(buf, 0, numRead);
  }
  
}
