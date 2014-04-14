/*
 * File created on Apr 13, 2014 
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
package org.soulwing.credo.service.credential;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

import java.util.Collections;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.TagEntity;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.TagService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.protect.CredentialProtectionService;

/**
 * Unit tests for {@link DelegatingCredentialEditorBean}.
 *
 * @author Carl Harris
 */
public class DelegatingCredentialEditorBeanTest {

  private static final Long REQUEST_ID = -1L;
  
  private static final String TAG = "someTag";
  
  private static final String GROUP_NAME = "someGroup";

  private static final String NEW_GROUP_NAME = "someOtherGroup";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRepository credentialRepository;
  
  @Mock
  private CredentialProtectionService protectionService;
  
  @Mock
  private TagService tagService;
  
  @Mock
  private Errors errors;
  
  @Mock
  private Credential credential;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  @Mock
  private UserGroup group;
  
  private DelegatingCredentialEditorBean editor = 
      new DelegatingCredentialEditorBean();
  
  @Before
  public void setUp() throws Exception {
    editor.credentialRepository = credentialRepository;
    editor.protectionService = protectionService;
    editor.tagService = tagService;
    editor.setDelegate(credential);
  }
  
  @Test
  public void testSaveWhenNoChangeInOwner() throws Exception {
    context.checking(tagsExpectations());
    context.checking(repositoryExpectations(returnValue(credential)));
    editor.save(errors);    
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testSaveWhenEntityNotFound() throws Exception {
    context.checking(repositoryExpectations(
        throwException(new IllegalArgumentException())));
    context.checking(new Expectations() { { 
      allowing(credential).getId();
      will(returnValue(REQUEST_ID));
      oneOf(errors).addError(with("id"), with(containsString("NotFound")),
         (Object[]) with(arrayContaining(REQUEST_ID)));
    } });

    editor.save(errors);    
  }

  @Test(expected = MergeConflictException.class)
  public void testSaveWhenMergeConflict() throws Exception {
    context.checking(repositoryExpectations(
        throwException(new OptimisticLockException())));
    context.checking(new Expectations() { {
      allowing(credential).getId();
      will(returnValue(REQUEST_ID));
      oneOf(credentialRepository).findById(with(REQUEST_ID));
      will(returnValue(credential));
      oneOf(errors).addWarning(with(containsString("MergeConflict")),
          with(emptyArray()));
    } });

    editor.save(errors);    
  }

  @Test
  public void testSaveWhenOwnerChanged() throws Exception {
    context.checking(ownerExpectations());
    context.checking(tagsExpectations());
    context.checking(repositoryExpectations(returnValue(credential)));
    editor.setOwner(NEW_GROUP_NAME);
    editor.save(errors);    
  }
  
  private Expectations ownerExpectations() throws Exception {
    return new Expectations() { {
      allowing(credential).getOwner();
      will(returnValue(group));
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(protectionService).unprotect(with(same(credential)), with(
         allOf(any(ProtectionParameters.class), 
             hasProperty("groupName", equalTo(GROUP_NAME)))));
      will(returnValue(privateKey));
      oneOf(protectionService).protect(with(credential), with(privateKey),
          with(allOf(any(ProtectionParameters.class), 
              hasProperty("groupName", equalTo(NEW_GROUP_NAME)))));
    } };
  }
  
  private Expectations tagsExpectations() throws Exception {
    final Set<? extends Tag> tags = Collections.singleton(new TagEntity(TAG));
    return new Expectations() { {
      oneOf(credential).getTags();
      will(returnValue(tags));
      oneOf(tagService).resolve(with(arrayContaining(TAG)));
      will(returnValue(tags));
      oneOf(credential).setTags(with(same(tags)));
    } };
  }

  private Expectations repositoryExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(credentialRepository).update(with(same(credential)));
      will(outcome);
    } };
  }
  
}

