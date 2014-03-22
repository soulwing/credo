/*
 * File created on Mar 22, 2014 
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

import java.util.Collection;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

/**
 * Utility methods for working with JSF Ajax functionality.
 *
 * @author Carl Harris
 */
class FacesAjaxUtil {

  /**
   * Resets rendered inputs on an Ajax request.  
   * <p>
   * This works around an issue when the form is submitted with one or more 
   * validation errors, allowing the rendered inputs to be properly updated on 
   * subsequent Ajax requests.
   */
  public static void resetRenderedInputs(FacesContext facesContext) {
    PartialViewContext partialViewContext = 
        facesContext.getPartialViewContext();
    Collection<String> renderIds = partialViewContext.getRenderIds();
    UIViewRoot viewRoot = facesContext.getViewRoot();
    for (String renderId : renderIds) {
      UIComponent component = viewRoot.findComponent(renderId);
      if (component instanceof EditableValueHolder) {
        EditableValueHolder input = (EditableValueHolder) component;
        input.resetValue();
      }
    }
  }

}
