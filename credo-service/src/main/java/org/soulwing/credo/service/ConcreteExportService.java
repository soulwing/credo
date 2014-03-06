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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.exporter.CredentialExporter;
import org.soulwing.credo.service.exporter.ExportFormat;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.protect.GroupAccessException;
import org.soulwing.credo.service.protect.UserAccessException;

/**
 * A concrete {@link ExportService} implementation.
 * 
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteExportService implements ExportService {

  @Inject
  protected CredentialService credentialService;

  @Inject
  @Any
  protected Instance<CredentialExporter> exporters;

  @Inject
  protected CredentialProtectionService protectionService;

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportRequest newExportRequest(Long credentialId)
      throws NoSuchCredentialException {
    return new ConcreteExportRequest(
        credentialService.findCredentialById(credentialId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public ExportPreparation prepareExport(ExportRequest request)
      throws ExportException, AccessDeniedException, PassphraseException {

    try {
      Credential credential = request.getCredential();
      ProtectionParametersWrapper protection =
          new ProtectionParametersWrapper(request.getProtectionParameters(),
              credential.getOwner().getName());
      PrivateKeyWrapper privateKey =
          protectionService.unprotect(credential, protection);
      CredentialExporter exporter = findExporter(request.getFormat());
      return exporter.exportCredential(request, privateKey);
    }
    catch (GroupAccessException ex) {
      throw new AccessDeniedException();
    }
    catch (UserAccessException ex) {
      throw new PassphraseException();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private CredentialExporter findExporter(String format) {
    ExportFormatQualifier qualifier = new ExportFormatQualifier(format);
    Instance<CredentialExporter> exporter = exporters.select(qualifier);
    if (exporter == null) {
      throw new IllegalArgumentException("unsupported format: " + format);
    }
    return exporter.get();
  }

  static class ExportFormatQualifier extends AnnotationLiteral<ExportFormat>
      implements ExportFormat {

    private static final long serialVersionUID = -1081540987292172502L;
    private final String value;

    ExportFormatQualifier(String value) {
      this.value = value;
    }

    @Override
    public String value() {
      return value;
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
    public String getLoginName() {
      return delegate.getLoginName();
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
