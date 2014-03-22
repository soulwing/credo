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

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.soulwing.credo.Password;
import org.soulwing.credo.resource.Bundle;

/**
 * A validator for a password.
 *
 * @author Carl Harris
 */
@FacesValidator("org.soulwing.credo.validators.Password")
public class PasswordValidator implements Validator, Serializable {

  private static final String PASSWORD_ATTR = "password";
  
  private static final long serialVersionUID = 7428124321407973883L;

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(FacesContext context, UIComponent component, 
      Object value) throws ValidatorException {
    Object passwordId = component.getAttributes().get(PASSWORD_ATTR);
    if (passwordId == null) {
      // the component ID is 'password' by default
      passwordId = PASSWORD_ATTR;  
    }
    
    UIInput passwordComponent = (UIInput) component.findComponent(
        passwordId.toString());
    if (passwordComponent == null) {
      throw new IllegalArgumentException(
          "cannot find the a component with ID '" + passwordId + "'; "
          + " specify the '" + PASSWORD_ATTR 
          + "' attribute to identify the password component");
    }
    
    Password passwordAgain = (Password) value;
    Password password = (Password) passwordComponent.getLocalValue();
    if (password == null || password.isEmpty()) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.get(context.getViewRoot().getLocale()).getString(
              "passwordRequired"), null));              
    }
    if (!password.equals(passwordAgain)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.get(context.getViewRoot().getLocale()).getString(
              "passwordValidationFailed"), null));              
    }
  }

}
