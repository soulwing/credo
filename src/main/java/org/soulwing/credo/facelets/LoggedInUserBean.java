/*
 * File created on Mar 17, 2014 
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

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.UserProfile;
import org.soulwing.credo.service.UserProfileService;

/**
 * A bean that provides information about the logged in user for the 
 * navigation bar.
 *
 * @author Carl Harris
 */
@Named
@SessionScoped
public class LoggedInUserBean implements Serializable {

  private static final long serialVersionUID = -990929418593901243L;
  
  @Inject
  protected UserProfileService profileService;
    
  private UserProfile profile;
  
  /**
   * Initializes the receiver.
   */
  @PostConstruct
  public void init() {
    profile = profileService.getLoggedInUserProfile();
  }
  
  /**
   * Gets the name of the logged in user.
   * @return name
   */
  public String getName() {
    return profile.getFullName();
  }
  
}
