/*
 * File created on Apr 14, 2014 
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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.lang.StringUtils;
import org.soulwing.credo.resource.Bundle;

/**
 * A converter for inputs of type {@link X500Principal}.
 *
 * @author Carl Harris
 */
public class X500PrincipalConverter implements Converter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, 
      String text) {
    try {
      if (StringUtils.isBlank(text)) return null;
      return new X500Principal(text);
    }
    catch (IllegalArgumentException ex) {
      throw new ConverterException(new FacesMessage(
          FacesMessage.SEVERITY_ERROR, Bundle.getString(
              "subjectNameInvalid", context.getViewRoot().getLocale()), 
          null), ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, 
      Object value) {
    if (value == null) return "";
    return value.toString();
  }

}
