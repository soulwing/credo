/*
 * File created on Apr 12, 2014 
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

import java.io.IOException;

import javax.ejb.Local;

import org.soulwing.credo.service.FileDownloadResponse;
import org.soulwing.credo.service.NoSuchCredentialException;

/**
 * A service that provides support for downloading a credential request object
 * via a user interaction.
 *
 * @author Carl Harris
 */
@Local
public interface DownloadCredentialRequestService {

  /**
   * Transfers a request to a client via the given response object.
   * @param requestId unique identifier of the request to download
   * @param response response object that will be used to transfer the
   *    request content
   * @throws IOException if an error occurs in transferring the 
   *    request content to the remote client
   */
  void downloadRequest(Long requestId,
      FileDownloadResponse response) 
      throws NoSuchCredentialException, IOException;
  
}
