/*
 * File created on Feb 28, 2014 
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
package org.soulwing.credo.facelets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.service.UserProfilePreparation;
import org.soulwing.credo.service.UserProfileService;

/**
 * A bean that supports the Create User Profile interaction.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class CreateUserProfileBean {

  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  @Inject
  protected FacesContext facesContext;
  
  @Inject
  protected UserProfileService userProfileService;

  private UserProfilePreparation preparation;
  private String passwordAgain;

  @PostConstruct
  public void init() {
    preparation = userProfileService.prepareProfile(
        facesContext.getExternalContext().getRemoteUser());
  }
  
  /**
   * Gets the login name for the profile that will be created.
   * @return login name
   */
  public String getLoginName() {
    Validate.notNull(preparation, "not prepared");
    return preparation.getLoginName();
  }

  /**
   * Gets the full name for the profile that will be created.
   * @return full name or {@code null} if none has been set
   */
  public String getFullName() {
    Validate.notNull(preparation, "not prepared");
    return preparation.getFullName();
  }

  /**
   * Sets the full name for the profile that will be created.
   * @param fullName the full name to set
   */
  public void setFullName(String fullName) {
    Validate.notNull(preparation, "not prepared");
    preparation.setFullName(fullName);
  }

  /**
   * Gets the password for the profile that will be created.
   * @return password or {@code null} if none has been set
   */
  public String getPassword() {
    Validate.notNull(preparation, "not prepared");
    char[] password = preparation.getPassword();
    if (password == null) return null;
    return new String(password);
  }

  /**
   * Gets the password for the profile that will be created.
   * @param password the password to set
   */
  public void setPassword(String password) {
    Validate.notNull(preparation, "not prepared");
    preparation.setPassword(password.toCharArray());
  }

  /**
   * Gets the password verification property.
   * @return alleged duplicate of the password property or {@code null}
   *   if none has been set
   */
  public String getPasswordAgain() {
    return passwordAgain;
  }

  /**
   * Sets the password verification property.
   * @param passwordAgain the value to set
   */
  public void setPasswordAgain(String passwordAgain) {
    this.passwordAgain = passwordAgain;
  }

  /**
   * Gets the user profile preparation object.
   * <p>
   * This method is exposed to support unit testing.
   * @return preparation
   */
  UserProfilePreparation getPreparation() {
    return preparation;
  }

  /**
   * Sets the user profile preparation object.
   * <p>
   * This method is exposed to support unit testing.
   * @param preparation the preparation to set
   */
  void setPreparation(UserProfilePreparation preparation) {
    this.preparation = preparation;
  }

  /**
   * Creates the user profile specified in the form.
   * @return outcome ID
   */
  public String createProfile() {
    Validate.notNull(preparation, "not prepared");
    userProfileService.createProfile(preparation);
    return SUCCESS_OUTCOME_ID;
  }
  
  /**
   * Cancels the user profile creation.
   * @return outcome ID
   */
  public String cancel() {
    return CANCEL_OUTCOME_ID;
  }
  
}
