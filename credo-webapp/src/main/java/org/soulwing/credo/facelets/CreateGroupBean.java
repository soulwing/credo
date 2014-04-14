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

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.group.EditException;
import org.soulwing.credo.service.group.GroupService;
import org.soulwing.credo.service.group.NoSuchGroupException;

/**
 * A bean that supports the Create Group interaction.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class CreateGroupBean {

  static final String SUCCESS_OUTCOME_ID = "success";
  
  static final String CANCEL_OUTCOME_ID = "cancel";
  
  static final String PASSWORD_OUTCOME_ID = "password";
  
  @Inject
  protected GroupService groupService;
  
  @Inject
  protected Errors errors;
 
  @Inject
  protected DelegatingGroupEditor editor;
  
  @Inject
  protected PasswordFormEditor passwordEditor;
 
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
    return CANCEL_OUTCOME_ID;
  }
  
  /**
   * Performs the Save action.
   * @return outcome ID
   */
  public String save() {
    try {
      groupService.saveGroup(editor.getDelegate(), errors);
      return SUCCESS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      passwordEditor.setGroupName(editor.getOwner());
      return PASSWORD_OUTCOME_ID;
    }
    catch (EditException ex) {
      return null;
    }
    catch (GroupAccessException ex) {
      return null;
    }
    catch (MergeConflictException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchGroupException ex) {
      throw new RuntimeException(ex);
    }
  }
}
