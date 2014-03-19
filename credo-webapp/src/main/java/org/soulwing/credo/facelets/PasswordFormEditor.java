/*
 * File created on Mar 19, 2014 
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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.Dependent;
import javax.faces.event.ValueChangeEvent;

import org.soulwing.credo.Password;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.crypt4j.Crypt;

/**
 * A password field editor.
 *
 * @author Carl Harris
 */
@Dependent
public class PasswordFormEditor  
    implements ProtectionParameters, Serializable {

  private static final long serialVersionUID = 3933762758500375291L;

  private String groupName;
  private Password password;
  private String expected;
  private boolean correct;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getGroupName() {
    return groupName;
  }

  /**
   * Sets the group name.
   * @param groupName the group name to set.
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Password getPassword() {
    return password;
  }

  /**
   * Sets the password.
   * @param password the password to set
   */
  public void setPassword(Password password) {
    this.password = password;
  }

  /**
   * Gets the expected password.
   * @return encrypted password string
   */
  public String getExpected() {
    return expected;
  }

  /**
   * Sets the expected password.
   * @param expected the encrypted password string to set
   */
  public void setExpected(String expected) {
    this.expected = expected;
  }

  /**
   * Tests whether the password property contains the expected password.
   * @return {@code true} if the passwords match
   */
  public boolean isCorrect() {
    return correct;
  }

  /**
   * Sets the {@code correct} status
   * <p>
   * This method is a no-op to satisfy the need for the JSF {@code inputHidden}
   * component to have a writeable property.
   * @param correct ignored parameter
   */
  public void setCorrect(boolean correct) {
    // no-op
  }
  
  /**
   * Notifies the recipient that the password property value will change.
   * @param event event object
   */
  public void passwordValueChanged(ValueChangeEvent event) {    
    Password password = (Password) event.getNewValue();
    try {
      String actual = (password == null || password.isEmpty()) ? 
          "" : Crypt.crypt(password.toCharArray(), expected);
      correct = actual.equals(expected);
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }

}
