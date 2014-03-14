/*
 * File created on Mar 14, 2014 
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
package org.soulwing.credo.service.group;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.SecretKeyWrapper;

/**
 * Unit tests for {@link NewGroupEditor}.
 *
 * @author Carl Harris
 */
public class NewGroupEditorTest 
    extends AbstractGroupEditorTest<NewGroupEditor> {

  @Mock
  private KeyGeneratorService keyGeneratorService;
  
  @Mock
  private SecretKeyWrapper secretKey;
  
  @Override
  protected NewGroupEditor newEditor() {
    return new NewGroupEditor();
  }

  @Override
  public void onSetUp(NewGroupEditor editor) throws Exception {
    editor.keyGeneratorService = keyGeneratorService;
  }
  
  @Override
  protected Expectations protectionExpectations(final int memberCount) {
    return new Expectations() { { 
      oneOf(keyGeneratorService).generateSecretKey();
      will(returnValue(secretKey));
      between(1, memberCount).of(protectionService).protect(
          with(same(group)), with(same(secretKey)), with(same(profile)));
    } };
  }
  
  
}
