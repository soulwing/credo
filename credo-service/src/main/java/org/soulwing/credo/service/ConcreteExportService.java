/*
 * File created on Feb 24, 2014 
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

import java.io.IOException;
import java.util.Collection;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.ExportFormat.Variant;
import org.soulwing.credo.service.crypto.PasswordGenerator;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.CredentialExporter;
import org.soulwing.credo.service.exporter.CredentialExporterRegistry;
import org.soulwing.credo.service.protect.CredentialProtectionService;

/**
 * A concrete {@link ExportService} implementation.
 * 
 * @author Carl Harris
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConcreteExportService implements ExportService {

  @Inject
  protected CredentialService credentialService;

  @Inject
  protected CredentialExporterRegistry exporterRegistry;
  
  @Inject
  protected CredentialProtectionService protectionService;

  @Inject
  protected PasswordGenerator passwordGenerator;
  
  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ExportRequest newExportRequest(Long credentialId)
      throws NoSuchCredentialException {
    return new ConcreteExportRequest(
        credentialService.findCredentialById(credentialId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<ExportFormat> getFormats() {
    return exporterRegistry.getFormats();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportFormat getDefaultFormat() {
    return exporterRegistry.getDefaultFormat();        
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Variant> getVariants(String format) {
    return exporterRegistry.getVariants(format);
  }


  @Override
  public ExportFormat findFormat(String id) {
    return exporterRegistry.findFormat(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Password generatePassphrase() {
    return passwordGenerator.generatePassword();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ExportPreparation prepareExport(ExportRequest request, Errors errors)
      throws ExportException, GroupAccessException, PassphraseException {

    try {
      Credential credential = request.getCredential();
      ProtectionParametersWrapper protection =
          new ProtectionParametersWrapper(request.getProtectionParameters(),
              credential.getOwner().getName());
      PrivateKeyWrapper privateKey =
          protectionService.unprotect(credential, protection);
      CredentialExporter exporter = exporterRegistry.findExporter(request);
      return exporter.exportCredential(request, privateKey);
    }
    catch (OwnerAccessControlException|GroupAccessException ex) {
      String groupName = (ex instanceof OwnerAccessControlException) ?
          ((OwnerAccessControlException) ex).getGroupName()
          : ((GroupAccessException) ex).getGroupName();          
      errors.addError("groupAccessDenied", new Object[] { groupName });
      throw new GroupAccessException(groupName);
    }
    catch (PassphraseException|UserAccessException ex) {
      errors.addError("passphrase", "passphraseIncorrect");
      throw new PassphraseException();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * A wrapper for a {@link ProtectionParameters} that overrides the specified
   * group with a given value.
   * <p>
   * This wrapper is used to ensure that the credential's owner group is used
   * instead of the value provided by the caller.
   */
  public static class ProtectionParametersWrapper
      implements ProtectionParameters {

    private final ProtectionParameters delegate;
    private final String groupName;

    /**
     * Constructs a new instance.
     * @param delegate
     * @param groupName
     */
    public ProtectionParametersWrapper(ProtectionParameters delegate,
        String groupName) {
      this.delegate = delegate;
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
      return delegate.getPassword();
    }

  }

}
