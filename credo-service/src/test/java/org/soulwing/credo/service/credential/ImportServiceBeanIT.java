/*
 * File created on Feb 17, 2014 
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.soulwing.credo.Credential;
import org.soulwing.credo.Password;
import org.soulwing.credo.UserGroup;
import org.soulwing.credo.domain.CredentialEntity;
import org.soulwing.credo.logging.LoggerCategory;
import org.soulwing.credo.repository.CredentialRepository;
import org.soulwing.credo.security.OwnerAccessControlException;
import org.soulwing.credo.service.Errors;
import org.soulwing.credo.service.FileContentModel;
import org.soulwing.credo.service.MockUserContextService;
import org.soulwing.credo.service.ProtectionParameters;
import org.soulwing.credo.service.UserProfilePreparation;
import org.soulwing.credo.service.UserProfileService;
import org.soulwing.credo.service.archive.ArchiveBuilder;
import org.soulwing.credo.service.credential.ImportServiceBean;
import org.soulwing.credo.service.credential.CredentialService;
import org.soulwing.credo.service.credential.ImportDetails;
import org.soulwing.credo.service.credential.ImportService;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.crypto.bc.BcPrivateKeyWrapper;
import org.soulwing.credo.service.crypto.jca.JcaPrivateKeyWrapper;
import org.soulwing.credo.service.exporter.CredentialExporter;
import org.soulwing.credo.service.group.GroupEditorFactory;
import org.soulwing.credo.service.importer.CredentialImporter;
import org.soulwing.credo.service.pem.PemObjectBuilder;
import org.soulwing.credo.service.pem.bc.BcPemObjectBuilder;
import org.soulwing.credo.service.protect.CredentialProtectionService;
import org.soulwing.credo.service.request.CredentialRequestEditorFactory;

/**
 * Integration tests for {@link ImportServiceBean}.
 * 
 * @author Carl Harris
 */
@RunWith(Arquillian.class)
public class ImportServiceBeanIT {

  private static final String LOGIN_NAME = "someUser";

  @Deployment
  public static Archive<?> createDeployment() {
    WebArchive archive = ShrinkWrap.create(WebArchive.class)
        .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
            .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile())
        .addPackage(Credential.class.getPackage())
        .addPackage(CredentialEntity.class.getPackage())
        .addPackage(CredentialRepository.class.getPackage())
        .addPackage(LoggerCategory.class.getPackage())
        .addPackage(OwnerAccessControlException.class.getPackage())
        .addPackage(Errors.class.getPackage())
        .addPackage(CredentialService.class.getPackage())
        .addPackage(ArchiveBuilder.class.getPackage())
        .addPackage(CredentialExporter.class.getPackage())
        .addPackage(CredentialImporter.class.getPackage())
        .addPackage(PrivateKeyWrapper.class.getPackage())
        .addPackage(BcPrivateKeyWrapper.class.getPackage())
        .addPackage(JcaPrivateKeyWrapper.class.getPackage())
        .addPackage(PemObjectBuilder.class.getPackage())
        .addPackage(BcPemObjectBuilder.class.getPackage())
        .addPackage(GroupEditorFactory.class.getPackage())
        .addPackage(CredentialProtectionService.class.getPackage())
        .addPackage(CredentialRequestEditorFactory.class.getPackage())
        .addClass(MockUserContextService.class)
        .addAsResource("testcases")
        .addAsResource("persistence-test.xml", "META-INF/persistence.xml")
        .addAsResource("META-INF/orm.xml", "META-INF/orm.xml")
        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    System.out.println(archive.toString(true));
    return archive;
  }
  
  @Inject
  private UserProfileService profileService;
  
  @Inject
  private ImportService importService;

  @Inject
  private MockUserContextService userContextService;
  
  private Errors errors = new SimpleErrors();
  
  @Before
  public void init() {
    userContextService.setLoginName(LOGIN_NAME);
  }
  
  @Test
  public void testImportWithUnencryptedKey() throws Exception {    
    final String testCase = "unencrypted-key";
    Properties properties = properties(testCase);
    
    ImportDetails details = 
        importService.prepareImport(testFiles(testCase), null, errors);
    
    validateDetails(details, properties);
  }

  @Test
  public void testImportWithPemEncryptedKey() throws Exception {    
    final String testCase = "pem-encrypted-key";
    Properties properties = properties(testCase);
    Password passphrase = new Password(
        properties.getProperty("passphrase").toCharArray());
    
    ImportDetails details = 
        importService.prepareImport(testFiles(testCase), passphrase, errors);
    validateDetails(details, properties);
  }


  @Test
  public void testImportWithPKCS8Key() throws Exception {    
    final String testCase = "pkcs8-key";
    Properties properties = properties(testCase);
    Password passphrase = new Password(
        properties.getProperty("passphrase").toCharArray());
    
    ImportDetails details = 
        importService.prepareImport(testFiles(testCase), passphrase, errors);
    validateDetails(details, properties);
  }

  private void validateDetails(ImportDetails details, Properties properties) {
    assertThat(details, is(not(nullValue())));
    assertThat(details.getSubjectCommonName(), is(equalTo(
        properties.getProperty("subject"))));

    assertThat(details.getPrivateKey(), is(not(nullValue())));
    assertThat(details.getCertificates(), is(not(empty())));
    assertThat(details.getCertificates().get(0).getSubject().getName(), 
        containsString(properties.getProperty("subject")));
    assertThat(details.getCertificates().get(1).getSubject().getName(), 
        containsString(properties.getProperty("issuer1")));
    assertThat(details.getCertificates().get(2).getSubject().getName(), 
        containsString(properties.getProperty("issuer2")));
  }
  
  @Test
  public void testImportProtectAndSave() throws Exception {
    final String testCase = "pkcs8-key";
    final Password password = new Password("somePassword".toCharArray());
    final ProtectionParameters protection = newProtectionParameters(password);
    final Properties properties = properties(testCase);
    final Password importPassphrase = new Password(
        properties.getProperty("passphrase").toCharArray());

    createUserProfile(LOGIN_NAME, password);

    ImportDetails details = 
        importService.prepareImport(testFiles(testCase), importPassphrase, errors);
    validateDetails(details, properties);
    
    Credential credential = importService.createCredential(details, 
        protection, errors);
    credential.setName(details.getSubjectCommonName());
    importService.saveCredential(credential, false, errors);
  }

  private void createUserProfile(String loginName, Password password) {
    UserProfilePreparation preparation = 
        profileService.prepareProfile(loginName);
    preparation.setFullName("Some User");
    preparation.setPassword(password);
    profileService.createProfile(preparation);
  }

  private ProtectionParameters newProtectionParameters(
      final Password password) {
    return new ProtectionParameters() {

      @Override
      public String getGroupName() {
        return UserGroup.SELF_GROUP_NAME;
      }
      @Override
      public Password getPassword() {
        return password;
      } 
      
    };
  }
 
  private List<FileContentModel> testFiles(String testCase) 
      throws Exception {
    List<FileContentModel> files = new ArrayList<>();
    files.add(new ResourceContent(getResource(path(testCase, ".key"))));
    files.add(new ResourceContent(getResource(path(testCase, ".crt"))));
    files.add(new ResourceContent(getResource(path(testCase, "-ca.crt"))));
    return files;
  }
  
  private Properties properties(String testCase) throws Exception {
    URL url = getResource(path(testCase, ".properties"));
    try (InputStream inputStream = url.openStream()) {
      Properties properties = new Properties();
      properties.load(inputStream);
      return properties;
    }
  }
  
  private String path(String testCase, String suffix) {
    return String.format("testcases/%s/test%s", testCase, suffix);
  }
  
  private URL getResource(String name) throws Exception {
    URL url = getClass().getClassLoader().getResource(name);
    if (url == null) {
      throw new FileNotFoundException(name);
    }
    return url;    
  }
  
  static class ResourceContent implements FileContentModel {

    private final URL url;
    
    /**
     * Constructs a new instance.
     * @param url
     */
    public ResourceContent(URL url) {
      this.url = url;
    }

    @Override
    public String getName() {
      return url.getFile();
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return url.openStream();        
    }
    
  }
  
  static class SimpleErrors implements Errors {

    private static final long serialVersionUID = -2423212313793765585L;
    
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    
    @Override
    public boolean hasErrors() {
      return !errors.isEmpty();
    }

    @Override
    public boolean hasWarnings() {
      return !warnings.isEmpty();
    }

    @Override
    public void addError(String message, Object... args) {
      errors.add(message);
    }

    @Override
    public void addError(String clientId, String message, Object... args) {
      errors.add(message);
    }

    @Override
    public void addWarning(String message, Object... args) {
      warnings.add(message);
    }

    @Override
    public void addWarning(String clientId, String message, Object... args) {
      warnings.add(message);
    }
    
  }

}
