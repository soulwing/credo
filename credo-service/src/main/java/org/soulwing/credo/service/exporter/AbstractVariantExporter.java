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
package org.soulwing.credo.service.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.soulwing.credo.service.ExportFormat;

/**
 * An abstract base for {@link CredentialExporter} objects must select a 
 * variant.
 *
 * @author Carl Harris
 */
public abstract class AbstractVariantExporter<T extends ExportFormat.Variant> 
    extends AbstractCredentialExporter {

  @Inject
  private Instance<T> variants;

  /**
   * Constructs a new instance.
   * @param id format identifier
   * @param passphraseRequired
   */
  protected AbstractVariantExporter(String id, boolean passphraseRequired) {
    super(id, passphraseRequired);
  }

  /**
   * Sets the {@code variants} property.
   * <p>
   * This method is exposed to support unit testing.
   * @param variants the variants to set
   */
  public void setVariants(Instance<T> variants) {
    this.variants = variants;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Variant> getVariants() {
    List<Variant> v = new ArrayList<>();
    Iterator<T> i = variants.iterator();
    while (i.hasNext()) {
      v.add(i.next());
    }
    
    Collections.sort(v, new Comparator<Variant>() {
      @Override
      public int compare(Variant a, Variant b) {
        return a.getName().compareTo(b.getName());
      } 
    });
    
    return v;
  }

  /**
   * Finds the variant with the given identifier.
   * @param id of the variant to find
   * @return matching variant
   * @throws IllegalArgumentException if no variant exists with the given
   *    identifier
   */
  protected T findVariant(String id) {
    Iterator<T> i = variants.iterator();
    while (i.hasNext()) {
      T variant = i.next();
      if (variant.getId().equals(id)) {
        return variant;
      }
    }
    throw new IllegalArgumentException("unrecognized variant: " + id);
  }

}
