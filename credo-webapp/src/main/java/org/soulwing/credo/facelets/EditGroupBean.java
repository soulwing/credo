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

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.EditException;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

/**
 * A bean that supports the Edit Group interaction.
 *
 * @author Carl Harris
 */
@Named
@ConversationScoped
public class EditGroupBean implements Serializable {

  private static final long serialVersionUID = -8207806031631775455L;

  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  static final String PASSWORD_OUTCOME_ID = "password";
  
  static final String FAILURE_OUTCOME_ID = "failure";
  
  @Inject
  protected Conversation conversation;
  
  @Inject
  protected GroupService groupService;
  
  @Inject
  protected Errors errors;

  @Inject
  protected DelegatingGroupEditor editor;
  
  @Inject
  protected PasswordFormEditor passwordEditor;

  private Long id;
 
  /**
   * Gets the {@code id} property.
   * @return
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the {@code id} property.
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the editor for the group to create.
   * @return editor
   */
  public DelegatingGroupEditor getEditor() {
    return editor;
  }
  
  /**
   * Gets the password editor.
   * @return editor
   */
  public PasswordFormEditor getPasswordEditor() {
    return passwordEditor;
  }

  /**
   * Action that is invoked when the edit form is initially displayed.
   * @return outcome ID
   */
  public String createEditor() {
    if (id == null) {
      errors.addError("id", "groupIdIsRequired");
      return null;
    }
    try {
      editor.setDelegate(groupService.editGroup(id));
      passwordEditor.setGroupName(editor.getName());
      beginConversation();
      return null;
    }
    catch (NoSuchGroupException ex) {
      errors.addError("id", "groupNotFound");
      endConversation();
      return FAILURE_OUTCOME_ID;
    }
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
      return PASSWORD_OUTCOME_ID;
    }
    catch (MergeConflictException ex) {
      return createEditor();
    }
    catch (EditException ex) {
      return null;
    }
    catch (GroupAccessException|NoSuchGroupException ex) {
      endConversation();
      return FAILURE_OUTCOME_ID;
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
