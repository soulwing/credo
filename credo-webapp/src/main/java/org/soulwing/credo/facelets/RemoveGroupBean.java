/*
 * File created on Mar 15, 2014 
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

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupDetail;
import org.soulwing.credo.service.EditException;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.NoSuchGroupException;

/**
 * A bean that supports the Remove Group interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class RemoveGroupBean implements Serializable {

  private static final long serialVersionUID = 8029486156502254270L;

  static final String SUCCESS_OUTCOME_ID = "success";
  static final String FAILURE_OUTCOME_ID = "failed";
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  @Inject
  protected GroupService groupService;
  
  @Inject
  protected Errors errors;
  
  @Inject
  protected Conversation conversation;
  
  private Long id;

  private GroupDetail group;
  
  /**
   * Gets the unique identifier of the group to remove.
   * @return unique identifier or {@code null} if none has been set
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the group to remove.
   * @param id the unique identifier to set
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the group to remove.
   * @return group detail
   */
  public GroupDetail getGroup() {
    return group;
  }

  /**
   * Finds the group to remove.
   * <p>
   * This action is fired when the view is first displayed.
   * @return outcome ID
   */
  public String findGroup() {
    if (id == null) {
      errors.addError("id", "groupIdIsRequired");
      return FAILURE_OUTCOME_ID;
    }
    try {
      group = groupService.findGroup(id);
      beginConversation();
      return null;
    }
    catch (NoSuchGroupException ex) {
      return FAILURE_OUTCOME_ID;
    }
  }
  
  /**
   * Cancels the request to remove the group.
   * @return outcome ID
   */
  public String cancel() {
    endConversation();
    return CANCEL_OUTCOME_ID;
  }
  
  /**
   * Removes the selected group.
   * @return outcome ID
   */
  public String remove() {
    try {
      groupService.removeGroup(id, errors);
      return SUCCESS_OUTCOME_ID;
    }
    catch (EditException ex) {
      return FAILURE_OUTCOME_ID;
    }
    catch (NoSuchGroupException ex) {
      return FAILURE_OUTCOME_ID;
    }
    finally {
      endConversation();
    }
  }

  private void beginConversation() {
    if (!conversation.isTransient()) return;
    conversation.begin();
  }
  
  private void endConversation() {
    if (conversation.isTransient()) return;
    conversation.end();
  }

}
