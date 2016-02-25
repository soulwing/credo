/*
 * File created on Mar 12, 2014 
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
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.group.CreateGroupService;
import org.soulwing.credo.service.group.EditException;
import org.soulwing.credo.service.group.NoSuchGroupException;

/**
 * A bean that supports the Create Group interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class CreateGroupBean implements Serializable {

  private static final long serialVersionUID = 8496000879655564569L;

  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  static final String PASSWORD_OUTCOME_ID = "password";
  
  @Inject
  protected CreateGroupService groupService;
  
  @Inject
  protected Errors errors;
 
  @Inject
  protected DelegatingGroupEditor editor;
  
  @Inject
  protected PasswordFormEditor passwordEditor;
 
  @Inject
  protected Conversation conversation;
  
  
  @PostConstruct
  public void init() {
    editor.setDelegate(groupService.newGroup());
  }
  
  /**
   * Gets the editor for the group to create.
   * @return editor
   */
  public DelegatingGroupEditor getEditor() {
    return editor;
  }
  
  /**
   * Gets the password form editor.
   * @return editor
   */
  public PasswordFormEditor getPasswordEditor() {
    return passwordEditor;
  }

  /**
   * Performs the Cancel action.
   * @return outcome ID
   */
  public String cancel() {
    endConversation();
    return CANCEL_OUTCOME_ID;
  }
  
  /**
   * Performs the Save action.
   * @return outcome ID
   */
  public String save() {
    try {
      editor.setPassword(passwordEditor.getPassword());
      groupService.saveGroup(editor.getDelegate(), errors);
      endConversation();
      return SUCCESS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      beginConversation();
      passwordEditor.setGroupName(editor.getOwner());
      return PASSWORD_OUTCOME_ID;
    }
    catch (EditException|GroupAccessException ex) {    
      beginConversation();
      return null;
    }
    catch (MergeConflictException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchGroupException ex) {
      throw new RuntimeException(ex);
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
