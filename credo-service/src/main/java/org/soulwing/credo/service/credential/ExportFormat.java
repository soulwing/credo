/*
 * File created on Mar 7, 2014 
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

import java.security.KeyStore;
import java.util.Collection;

/**
 * A description of an export format.
 * <p>
 * An instance of this class provides details that are useful in presenting
 * an export format option in a user interface.
 *
 * @author Carl Harris
 */
public interface ExportFormat {
  
  /**
   * Gets the format identifier that is used in {@link ExportRequest} to 
   * request an export in this format
   * @return format identifier
   */
  String getId();
  
  /**
   * Gets the name of the export format as a message bundle identifier.
   * @return message bundle identifier
   */
  String getName();
  
  /**
   * Gets the description of the export format as a message bundle identifier.
   * @return message bundle identifier
   */
  String getDescription();
  
  /**
   * Tests whether this export format requires a passphrase.
   * <p>
   * All export formats <em>allow</em> a passphrase to be provided by the user
   * to protect a credential's private key.  This property indicates whether
   * the passphrase is <em>mandatory</em>.
   * @return
   */
  boolean isPassphraseRequired();
 
  /**
   * Tests whether this format is the default.
   * @return {@code true} if this variant is the default
   */
  boolean isDefault();
  
  /**
   * Gets the collection of variants for this format.
   * @return collection of variants
   */
  Collection<Variant> getVariants();
  
  /**
   * Gets the default variant for this format.
   * @return default variant.
   */
  Variant getDefaultVariant();
  
  /**
   * Finds a variant using its unique identifier.
   * @param id unique identifier of the variant to match
   * @return variant
   * @throws IllegalArgumentException if the variant does not exist
   */
  Variant findVariant(String id);
  
  /**
   * Describes a variant for an export format.
   * <p>
   * An {@link ExportFormat} describes a general format for exporting a 
   * credential.  This interfaces describes a minor variation.  For example,
   * a descriptor might specify a file containing a {@link KeyStore} as an
   * export format; variants might be used to specify supported key store 
   * types (e.g. JKS, PKCS12).
   */
  interface Variant {

    /**
     * Gets the variant identifier that is used in {@link ExportRequest} to 
     * request an export using this variant.
     * @return variant identifier
     */
    String getId();
    
    /**
     * Gets the name of the variant as a message bundle identifier.
     * @return message bundle identifier
     */
    String getName();
    
    /**
     * Gets the description of the variant as a message bundle identifier.
     * @return message bundle identifier
     */
    String getDescription();
    
    /**
     * Gets the filename suffix that is typically used for a file containing
     * an export of this format variant.
     * @return
     */
    String getSuffix();
    
    /**
     * Tests whether this variant is the default variant for the format.
     * @return {@code true} if this variant is the default variant
     */
    boolean isDefault();
    
  }
  
}
