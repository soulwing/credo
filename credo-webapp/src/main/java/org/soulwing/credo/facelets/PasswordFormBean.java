/*
 * File created on Mar 8, 2014 
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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.faces.event.ValueChangeEvent;

import org.soulwing.credo.Password;
import org.soulwing.crypt4j.Crypt;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public class PasswordFormBean extends ProtectionParametersBean {

  private String expected;
  private String actual;
  
  @Override
  public void setPassword(Password password) {
    super.setPassword(password);
  }

  public String getActual() {
    return actual;
  }

  public void setActual(String actual) {
    this.actual = actual;
  }

  public String getExpected() {
    return expected;
  }

  public void setExpected(String expected) {
    this.expected = expected;
  }
  
  public void passwordValueChanged(ValueChangeEvent event) {    
    passwordChanged((Password) event.getNewValue());
  }
  
  private void passwordChanged(Password password) {
    try {
      actual = (password == null || password.isEmpty()) ? 
          "" : Crypt.crypt(password.toCharArray(), expected);
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  

}
