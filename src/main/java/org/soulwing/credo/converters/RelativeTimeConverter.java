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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * A {@link Converter} that represents a date/time in the near past as a 
 * relative offset from the current time using common language.
 *
 * @author Carl Harris
 */
@FacesConverter("org.soulwing.credo.converters.RelativeTime")
public class RelativeTimeConverter implements Converter {

  static final int JUST_NOW_LIMIT = 60000;
  static final int MINS_AGO_LIMIT = 600000;
  static final int ROUNDED_MINS_AGO_LIMIT = 3600000;
  static final int HOURS_AGO_LIMIT = 4 * 3600000;
  
  private static final String PATTERN_WITHOUT_YEAR = "d MMM h:mm a";
  
  private static final String PATTERN_WITH_YEAR = "d MMM yyyy h:mm a";
  
  private static final String TODAY_PATTERN = "'today at' h:mm a";
  
  private static final String YESTERDAY_PATTERN = "'yesterday at' h:mm a";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, 
      String text) {
    throw new ConverterException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, 
      Object value) {
    Date then = (Date) value;
    if (then == null) return null;
        
    Date now = now();
    
    long thenTime = then.getTime();
    long nowTime = now.getTime();
    long diff = nowTime - thenTime;
    
    if (diff < JUST_NOW_LIMIT) {
      return "just now";
    }
    
    if (diff < 2 * JUST_NOW_LIMIT) {
      return "a minute ago";
    }
    
    if (diff < MINS_AGO_LIMIT) {
      return diff / (60 * 1000) + " minutes ago";
    }
    
    if (diff < ROUNDED_MINS_AGO_LIMIT) {
      return 5*(diff / (5 * 60 * 1000)) + " minutes ago";
    }

    if (diff < 2 * ROUNDED_MINS_AGO_LIMIT) {
      return "an hour ago";
    }

    if (diff < HOURS_AGO_LIMIT) {
      return diff / (60 * 60 * 1000) + " hours ago";
    }
    
    return dateFormat(then, now).format(then);
  }

  private DateFormat dateFormat(Date then, Date now) {
    String pattern = PATTERN_WITH_YEAR;
    if (isToday(then, now)) {
      pattern = TODAY_PATTERN;
    }
    else if (isYesterday(then, now)) {
      pattern = YESTERDAY_PATTERN;
    }
    else if (isLessThanOneYearAgo(then, now)) {
      pattern = PATTERN_WITHOUT_YEAR;
    }

    DateFormat df = new SimpleDateFormat(pattern);
    return df;
  }

  private boolean isToday(Date then, Date now) {
    Calendar thenCal = Calendar.getInstance();
    thenCal.setTime(then);
    Calendar nowCal = Calendar.getInstance();
    nowCal.setTime(now);
    nowCal.set(Calendar.HOUR_OF_DAY, 0);
    nowCal.set(Calendar.MINUTE, 0);
    nowCal.set(Calendar.SECOND, 0);
    nowCal.set(Calendar.MILLISECOND, 0);
    return !thenCal.before(nowCal);
  }
  
  private boolean isYesterday(Date then, Date now) {
    Calendar thenCal = Calendar.getInstance();
    thenCal.setTime(then);
    Calendar nowCal = Calendar.getInstance();
    nowCal.setTime(now);
    nowCal.set(Calendar.HOUR_OF_DAY, 0);
    nowCal.set(Calendar.MINUTE, 0);
    nowCal.set(Calendar.SECOND, 0);
    nowCal.set(Calendar.MILLISECOND, 0);
    nowCal.add(Calendar.DAY_OF_MONTH, -1);
    return !thenCal.before(nowCal);
  }
  
  private boolean isLessThanOneYearAgo(Date then, Date now) {
    Calendar thenCal = Calendar.getInstance();
    thenCal.setTime(then);
    Calendar nowCal = Calendar.getInstance();
    nowCal.setTime(now);
    nowCal.add(Calendar.YEAR, -1);
    nowCal.set(Calendar.DAY_OF_MONTH, 1);
    nowCal.add(Calendar.MONTH, 1);
    nowCal.set(Calendar.HOUR_OF_DAY, 0);
    nowCal.set(Calendar.MINUTE, 0);
    nowCal.set(Calendar.SECOND, 0);
    nowCal.set(Calendar.MILLISECOND, 0);
    return !thenCal.before(nowCal);
  }
  
  protected Date now() {
    return new Date();
  }
  
}
