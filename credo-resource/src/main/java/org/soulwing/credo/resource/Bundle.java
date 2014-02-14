/*
 * File created on Feb 13, 2014 
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
package org.soulwing.credo.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An abstraction of the main resource bundle.
 *
 * @author Carl Harris
 */
public class Bundle {

  /**
   * Gets the main resource bundle.
   * @return resource bundle
   */
  public static ResourceBundle get() {
    String base = Bundle.class.getPackage().getName() + ".messages";
    return ResourceBundle.getBundle(base);
  }
  
  /**
   * Gets a string from the resource bundle.
   * @param key key of the string to retrieve
   * @return string mapped to {@code code} or a placeholder if {@code key}
   *   does not exist in the bundle
   */
  public static String getString(String key) {
    try {
      return get().getString(key);
    }
    catch (MissingResourceException ex) {
      return "??" + key + "??";
    }
  }
  
}
