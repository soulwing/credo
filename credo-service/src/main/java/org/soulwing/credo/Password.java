/*
 * File created on Mar 6, 2014 
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
package org.soulwing.credo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * An immutable value holder for a password.
 *
 * @author Carl Harris
 */
public class Password implements Serializable {

  private static final long serialVersionUID = 5140371712391706218L;
  
  private final char[] value;

  /**
   * Constructs a new instance.
   * @param value
   */
  public Password(char[] value) {
    if (value == null) {
      throw new NullPointerException("value is required");
    }
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hashCode = 0;
    for (int i = 0; i < value.length; i++) {
      hashCode = 17*hashCode + (int) value[i];
    }
    return hashCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof Password)) return false;
    return Arrays.equals(this.value, ((Password) obj).value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "**********";
  }

  /**
   * Gets the content of the receiver as a character array.
   * @return char array containing the value of this password
   */
  public char[] toCharArray() {
    return value;
  }
  
}
