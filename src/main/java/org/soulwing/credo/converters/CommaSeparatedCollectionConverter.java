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

import java.util.Collection;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A converter that handles a collection by using comma-separated values.
 *
 * @author Carl Harris
 */
@FacesConverter("org.soulwing.credo.converters.CommaSeparated")
public class CommaSeparatedCollectionConverter implements Converter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, 
      String value) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, 
      Object value) {
    final Collection items = (Collection) value;
    final StringBuilder sb = new StringBuilder();
    final int max = items.size();
    final Iterator i = items.iterator();
    int index = 0;
    while (i.hasNext()) {
      sb.append(i.next());
      if (++index < max) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  
}
