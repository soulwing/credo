/*
 * File created on Mar 28, 2014 
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
package org.soulwing.credo.service;

/**
 * An exception thrown when attempting to update a persistent object whose
 * state indicates that a prior update was made and has not been merged.
 *
 * @author Carl Harris
 */
public class MergeConflictException extends Exception {

  private static final long serialVersionUID = 6519834371877669924L;

}
