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

import java.util.Date;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.lang.Validate;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.Tag;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.GroupAccessException;
import org.soulwing.credo.service.MergeConflictException;
import org.soulwing.credo.service.NoSuchGroupException;
import org.soulwing.credo.service.PassphraseException;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.TagService;
import org.soulwing.credo.service.UserAccessException;
import org.soulwing.credo.service.X500PrincipalUtil;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.group.GroupResolver;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.request.CredentialRequestException;
import org.soulwing.credo.service.request.DelegatingRequestEditor;

/**
 * A {@link DelegatingRequestEditor} implemented as a bean.
 *
 * @author Carl Harris
 */
@Dependent
public class DelegatingCredentialEditorBean
    implements DelegatingCredentialEditor, SaveableCredentialEditor {

  @Inject
  protected CredentialRepository credentialRepository;
  
  @Inject
  protected CredentialProtectionService protectionService;
  
  @Inject
  protected GroupResolver groupResolver;
  
  @Inject
  protected TagService tagService;
  
  private Credential delegate;
  private String owner;
  private String[] tags;
  private Password password;

  /**
   * {@inheritDoc}
   */
  @Override
  public Credential getDelegate() {
    return delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDelegate(Credential delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public X500Principal getSubject() {
    Validate.isTrue(!delegate.getCertificates().isEmpty());
    return new X500Principal(delegate.getCertificates().get(0).getSubject());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubjectCommonName() {
    return X500PrincipalUtil.getCommonName(getSubject());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getIssuerCommonName() {
    return delegate.getIssuer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getExpiration() {
    return delegate.getExpiration();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return delegate.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    delegate.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOwner() {
    if (owner != null) return owner;
    return delegate.getOwner().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getNote() {
    return delegate.getNote();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNote(String note) {
    delegate.setNote(note);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getTags() {
    if (tags == null) {
      Set<? extends Tag> tagSet = delegate.getTags();
      tags = new String[tagSet.size()];
      int i = 0;
      for (Tag tag : tagSet) {
        tags[i++] = tag.getText();
      }
    }
    return tags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTags(String[] tags) {
    this.tags = tags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Password getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassword(Password password) {
    this.password = password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(Errors errors)
      throws CredentialException, NoSuchCredentialException,
      GroupAccessException, PassphraseException, MergeConflictException {
    
    update(errors);
    protect(errors);
    delegate.setTags(tagService.resolve(getTags()));    
  }

  /**
   * Protects the request's private key
   * @param errors
   * @throws GroupAccessException
   * @throws PassphraseException
   * @throws CredentialRequestException
   */
  private void protect(Errors errors) throws GroupAccessException,
      PassphraseException, CredentialException {

    if (owner == null || owner.equals(delegate.getOwner().getName())) return;
    try {
      delegate.setOwner(groupResolver.resolveGroup(owner, errors));
      PrivateKeyWrapper privateKey = protectionService.unprotect(
          delegate, new ProtectionParametersBean(
              delegate.getOwner().getName()));
      protectionService.protect(delegate, privateKey, 
          new ProtectionParametersBean(owner));
    }
    catch (UserAccessException ex) {
      throw new PassphraseException();
    }
    catch (NoSuchGroupException ex) {
      errors.addError("owner", "groupNotFound");
      throw new CredentialException();
    }
  }
  
  /**
   * Updates the persistent request.
   * @param errors
   * @throws NoSuchCredentialException
   * @throws MergeConflictException
   */
  private void update(Errors errors) throws NoSuchCredentialException,
      MergeConflictException {
    try {
      delegate = credentialRepository.update(delegate);
    }
    catch (IllegalArgumentException ex) {
      errors.addError("id", "requestNotFound", delegate.getId());
      throw new NoSuchCredentialException();
    }
    catch (OptimisticLockException ex) {
      delegate = credentialRepository.findById(delegate.getId());
      errors.addWarning("requestMergeConflict");      
      throw new MergeConflictException();
    }
  }

  public class ProtectionParametersBean implements ProtectionParameters {

    private final String groupName;
    
    /**
     * Constructs a new instance.
     * @param groupName
     */
    public ProtectionParametersBean(String groupName) {
      this.groupName = groupName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGroupName() {
      return groupName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Password getPassword() {
      return password;
    }
    
  }

}
