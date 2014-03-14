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
package org.soulwing.credo.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.soulwing.credo.UserGroup;
import org.soulwing.credo.resource.Bundle;

/**
 * A validator for a group name.
 *
 * @author Carl Harris
 */
@FacesValidator("org.soulwing.credo.validators.GroupName")
public class GroupNameValidator implements Validator {

  private static final int MIN_LENGTH = 3;
  
  private static final int MAX_LENGTH = 30;
  
  private static final Pattern PATTERN = 
      Pattern.compile("^\\p{Alpha}[\\p{Alnum}_.+-]*$");
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void validate(FacesContext context, UIComponent component, 
      Object value) throws ValidatorException {
    
    String groupName = value.toString();
    Matcher matcher = PATTERN.matcher(groupName);
    
    if (!matcher.matches()) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.getString("groupNameInvalidCharacters", 
              FacesContext.getCurrentInstance().getViewRoot().getLocale()),
          null));
    }
    
    if (groupName.length() < MIN_LENGTH || groupName.length() > MAX_LENGTH) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.getString("groupNameInvalidLength", 
              FacesContext.getCurrentInstance().getViewRoot().getLocale()),
          null));
    }
    
    if (groupName.equalsIgnoreCase(UserGroup.SELF_GROUP_NAME)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
          Bundle.getString("groupNameCannotBeSelf", 
              FacesContext.getCurrentInstance().getViewRoot().getLocale()),
          null));
    }
    
  }

}
