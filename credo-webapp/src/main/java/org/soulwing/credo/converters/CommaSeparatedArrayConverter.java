/*
 * File created on Mar 14, 2014 
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A converter that handles a collection by using comma-separated values.
 *
 * @author Carl Harris
 */
@FacesConverter("org.soulwing.credo.converters.CommaSeparatedArray")
public class CommaSeparatedArrayConverter implements Converter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, 
      String value) {
    if (value == null) return null;
    return value.split("\\s*,\\s*");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, 
      Object value) {
    if (value == null) return null;
    final Object[] items =  (Object[]) value;
    final StringBuilder sb = new StringBuilder();
    for (int i = 0, max = items.length; i < max; i++) {
      sb.append(items[i].toString());
      if (i + 1 < max) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

}
