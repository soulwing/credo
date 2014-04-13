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
package org.soulwing.credo.service.request;

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
import org.soulwing.credo.CredentialRequest;
import org.soulwing.credo.Tag;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.TagEntity;
import org.soulwing.credo.repository.CredentialRequestRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.TagService;
import org.soulwing.credo.service.credential.NoSuchCredentialException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.protect.CredentialRequestProtectionService;

/**
 * Unit tests for {@link DelegatingRequestEditorBean}.
 *
 * @author Carl Harris
 */
public class DelegatingRequestEditorBeanTest {

  private static final Long REQUEST_ID = -1L;
  
  private static final String TAG = "someTag";
  
  private static final String GROUP_NAME = "someGroup";

  private static final String NEW_GROUP_NAME = "someOtherGroup";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialRequestRepository requestRepository;
  
  @Mock
  private CredentialRequestProtectionService protectionService;
  
  @Mock
  private TagService tagService;
  
  @Mock
  private Errors errors;
  
  @Mock
  private CredentialRequest request;
  
  @Mock
  private PrivateKeyWrapper privateKey;
  
  @Mock
  private UserGroup group;
  
  private DelegatingRequestEditorBean editor = new DelegatingRequestEditorBean();
  
  @Before
  public void setUp() throws Exception {
    editor.requestRepository = requestRepository;
    editor.protectionService = protectionService;
    editor.tagService = tagService;
    editor.setDelegate(request);
  }
  
  @Test
  public void testSaveWhenNoChangeInOwner() throws Exception {
    context.checking(tagsExpectations());
    context.checking(repositoryExpectations(returnValue(request)));
    editor.save(errors);    
  }

  @Test(expected = NoSuchCredentialException.class)
  public void testSaveWhenEntityNotFound() throws Exception {
    context.checking(repositoryExpectations(
        throwException(new IllegalArgumentException())));
    context.checking(new Expectations() { { 
      allowing(request).getId();
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
      allowing(request).getId();
      will(returnValue(REQUEST_ID));
      oneOf(requestRepository).findById(with(REQUEST_ID));
      will(returnValue(request));
      oneOf(errors).addWarning(with(containsString("MergeConflict")),
          with(emptyArray()));
    } });

    editor.save(errors);    
  }

  @Test
  public void testSaveWhenOwnerChanged() throws Exception {
    context.checking(ownerExpectations());
    context.checking(tagsExpectations());
    context.checking(repositoryExpectations(returnValue(request)));
    editor.setOwner(NEW_GROUP_NAME);
    editor.save(errors);    
  }
  
  private Expectations ownerExpectations() throws Exception {
    return new Expectations() { {
      allowing(request).getOwner();
      will(returnValue(group));
      allowing(group).getName();
      will(returnValue(GROUP_NAME));
      oneOf(protectionService).unprotect(with(same(request)), with(
         allOf(any(ProtectionParameters.class), 
             hasProperty("groupName", equalTo(GROUP_NAME)))));
      will(returnValue(privateKey));
      oneOf(protectionService).protect(with(request), with(privateKey),
          with(allOf(any(ProtectionParameters.class), 
              hasProperty("groupName", equalTo(NEW_GROUP_NAME)))));
    } };
  }
  
  private Expectations tagsExpectations() throws Exception {
    final Set<? extends Tag> tags = Collections.singleton(new TagEntity(TAG));
    return new Expectations() { {
      oneOf(request).getTags();
      will(returnValue(tags));
      oneOf(tagService).resolve(with(arrayContaining(TAG)));
      will(returnValue(tags));
      oneOf(request).setTags(with(same(tags)));
    } };
  }

  private Expectations repositoryExpectations(final Action outcome) 
      throws Exception {
    return new Expectations() { { 
      oneOf(requestRepository).update(with(same(request)));
      will(outcome);
    } };
  }
  
}

