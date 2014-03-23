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
import org.soulwing.credo.service.GroupEditException;
import org.soulwing.credo.service.GroupEditor;
import org.soulwing.credo.service.GroupService;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;

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
  
  @Inject
  protected GroupService groupService;
  
  @Inject
  protected Errors errors;
 
  private GroupEditor editor;
 

  @PostConstruct
  public void init() {
    editor = groupService.newGroup();
  }
  
  /**
   * Gets the editor for the group to create.
   * @return editor
   */
  public GroupEditor getEditor() {
    return editor;
  }
  
  /**
   * Sets the editor for the group to create.
   * <p>
   * This method is exposed to support unit testing.
   * @param editor the editor to set
   */
  void setEditor(GroupEditor editor) {
    this.editor = editor;
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
      groupService.saveGroup(editor, errors);
      return SUCCESS_OUTCOME_ID;
    }
    catch (PassphraseException ex) {
      throw new RuntimeException(ex);
    }
    catch (GroupEditException ex) {
      return null;
    }
    catch (GroupAccessException ex) {
      return null;
    }
    catch (NoSuchGroupException ex) {
      throw new RuntimeException(ex);
    }
  }
}
