/*
 * File created on Feb 14, 2014 
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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.soulwing.credo.resource.Bundle;
import org.soulwing.credo.service.Errors;

/**
 * A concrete {@link Errors} that delegates to a {@link FacesContext}. 
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class FacesContextErrors implements Errors {

  private static final long serialVersionUID = -5205778057768297264L;
  
  @Inject
  private FacesContext context;
  
  /**
   * Tests whether the faces context contains any errors.
   * @return {@code true} if the context contains at least one error
   */
  public boolean hasErrors() {
    return FacesMessage.SEVERITY_ERROR.equals(
        context.getMaximumSeverity());
  }

  /**
   * Tests whether the faces context contains any warnings.
   * @return {@code true} if the context contains at least one warning
   */
  public boolean hasWarnings() {
    return FacesMessage.SEVERITY_WARN.equals(
        context.getMaximumSeverity());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addError(String message, Object... args) {
    addError(null, message, args);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addError(String clientId, String message, Object... args) {
    context.addMessage(clientId, 
        createMessage(FacesMessage.SEVERITY_ERROR, message, args));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addWarning(String message, Object... args) {
    addWarning(null, message, args);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addWarning(String clientId, String message, Object... args) {
    context.addMessage(clientId, 
        createMessage(FacesMessage.SEVERITY_WARN, message, args));
  }

  /**
   * Creates a faces message object
   * @param severity message severity
   * @param message message bundle key
   * @param args arguments for message
   * @return faces message
   */
  private FacesMessage createMessage(FacesMessage.Severity severity, 
      String message, Object... args) {
    try {
      Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
      String pattern = Bundle.get(
          locale).getString(message);
      return new FacesMessage(severity,
          MessageFormat.format(pattern, args), null);
    }
    catch (MissingResourceException ex) {
      return new FacesMessage(severity, "???" + message + "???", null);
    }
  }

}
