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

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * A bean that supports the Add User interaction.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class AddUserBean {

  private String userName;
  private String fullName;
  private String password;
  private String passwordAgain;
  
  /**
   * Gets the {@code userName} property.
   * @return
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the {@code userName} property.
   * @param userName
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  /**
   * Gets the {@code fullName} property.
   * @return
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets the {@code fullName} property.
   * @param fullName
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Gets the {@code password} property.
   * @return
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the {@code password} property.
   * @param password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the {@code passwordAgain} property.
   * @return
   */
  public String getPasswordAgain() {
    return passwordAgain;
  }

  /**
   * Sets the {@code passwordAgain} property.
   * @param passwordAgain
   */
  public void setPasswordAgain(String passwordAgain) {
    this.passwordAgain = passwordAgain;
  }

  /**
   * Creates the user specified in the form.
   * @return the user to create
   */
  public String createUser() {
    return null;
  }
  
  /**
   * Cancels the add user interaction.
   * @return
   */
  public String cancel() {
    // TODO
    return null;
  }
  
}
