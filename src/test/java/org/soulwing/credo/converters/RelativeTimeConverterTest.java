/*
 * File created on Apr 20, 2014 
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
package org.soulwing.credo.converters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link RelativeTimeConverter}.
 *
 * @author Carl Harris
 */
public class RelativeTimeConverterTest {

  private Date now = new Date();
  private Calendar calendar = Calendar.getInstance();
  
  private RelativeTimeConverter converter = 
      new InstrumentedRelativeTimeConverter(now);
  
  @Before
  public void setUp() throws Exception {
    calendar.setTime(now);
  }
  
  @Test
  public void testJustNow() throws Exception {
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        is(equalTo("just now")));
  }
  
  @Test
  public void testJustNowAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.JUST_NOW_LIMIT + 1);
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        is(equalTo("just now")));
  }

  @Test
  public void testMinuteAgo() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.JUST_NOW_LIMIT);
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        is(equalTo("a minute ago")));
  }

  @Test
  public void testMinuteAgoAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -2*RelativeTimeConverter.JUST_NOW_LIMIT + 1);
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        is(equalTo("a minute ago")));
  }
  
  @Test
  public void testMinutesAgo() throws Exception {
    calendar.add(Calendar.MILLISECOND, -2*RelativeTimeConverter.JUST_NOW_LIMIT);
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        endsWith(" minutes ago"));
  }

  @Test
  public void testMinutesAgoAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.MINS_AGO_LIMIT + 1);
    assertThat(converter.getAsString(null, null, calendar.getTime()),
        endsWith(" minutes ago"));
  }

  @Test
  public void testRoundedMinutesAgo() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.MINS_AGO_LIMIT);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d?[05] minutes ago$"), is(true));
  }

  @Test
  public void testRoundedMinutesAgoAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.ROUNDED_MINS_AGO_LIMIT + 1);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d?[05] minutes ago$"), is(true));
  }

  @Test
  public void testHourAgo() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.ROUNDED_MINS_AGO_LIMIT);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text, is(equalTo("an hour ago")));
  }

  @Test
  public void testHourAgoAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -2*RelativeTimeConverter.ROUNDED_MINS_AGO_LIMIT + 1);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text, is(equalTo("an hour ago")));
  }

  @Test
  public void testHoursAgo() throws Exception {
    calendar.add(Calendar.MILLISECOND, -2*RelativeTimeConverter.ROUNDED_MINS_AGO_LIMIT);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d hours ago$"), is(true));
  }

  @Test
  public void testHoursAgoAtLimit() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.HOURS_AGO_LIMIT + 1);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d hours ago$"), is(true));
  }

  @Test
  public void testToday() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.HOURS_AGO_LIMIT);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^today at \\d+:\\d+ (AM|PM)$"), is(true));
  }

  @Test
  public void testYesterday() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.HOURS_AGO_LIMIT);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^yesterday at \\d+:\\d+ (AM|PM)$"), is(true));
  }

  @Test
  public void testSameYear() throws Exception {
    calendar.add(Calendar.MILLISECOND, -RelativeTimeConverter.HOURS_AGO_LIMIT);
    calendar.add(Calendar.DAY_OF_MONTH, -2);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d+ \\p{Alpha}+ \\d+:\\d+ (AM|PM)$"), is(true));
  }

  @Test
  public void testSameYearAtLimit() throws Exception {
    calendar.add(Calendar.YEAR, -1);
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d+ \\p{Alpha}+ \\d+:\\d+ (AM|PM)$"), is(true));
  }

  @Test
  public void testDifferentYear() throws Exception {
    calendar.add(Calendar.YEAR, -1);
    calendar.set(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.add(Calendar.MILLISECOND, -1);
    String text = converter.getAsString(null, null, calendar.getTime());
    assertThat(text.matches("^\\d+ \\p{Alpha}+ \\d{4} \\d+:\\d+ (AM|PM)$"), is(true));
  }

  private static class InstrumentedRelativeTimeConverter
      extends RelativeTimeConverter {
  
    private final Date now;
    
    InstrumentedRelativeTimeConverter(Date now) {
      this.now = now;
    }

    @Override
    protected Date now() {
      return now;
    }
    
  }
  
}
