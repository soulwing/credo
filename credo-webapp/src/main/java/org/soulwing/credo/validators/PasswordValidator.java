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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.soulwing.credo.resource.Bundle;

/**
 * A validator for a password.
 *
 * @author Carl Harris
 */
@FacesValidator("org.soulwing.credo.validators.Password")
public class PasswordValidator implements Validator {

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(FacesContext context, UIComponent component, 
      Object value) throws ValidatorException {
    String passwordAgain = value.toString();
    UIInput passwordComponent = (UIInput) component.findComponent("password");
    String password = passwordComponent.getLocalValue().toString();
    if (!password.equals(passwordAgain)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.get(context.getViewRoot().getLocale()).getString(
              "passwordValidationFailed"), null));              
    }    
  }

}
