/*
 * File created on Mar 1, 2014 
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
package org.soulwing.credo.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.soulwing.credo.resource.Bundle;

/**
 * A validator for a user name.
 *
 * @author Carl Harris
 */
@FacesValidator("org.soulwing.credo.validators.LoginName")
public class LoginNameValidator implements Validator {

  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 30;
  private static final Pattern PATTERN = 
      Pattern.compile("^\\p{Alpha}[\\p{Alnum}_.+-]*@?[\\p{Alnum}_.+-]+$");
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(FacesContext context, UIComponent component, 
      Object obj) throws ValidatorException {
    String value = obj.toString();
    Matcher matcher = PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.getString("userLoginNameInvalidCharacters", 
              FacesContext.getCurrentInstance().getViewRoot().getLocale()),
          null));
    }
    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.getString("userLoginNameInvalidLength", 
              FacesContext.getCurrentInstance().getViewRoot().getLocale()),
          null));
    }
  }

}
