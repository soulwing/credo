/*
 * File created on Mar 23, 2014 
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
package org.soulwing.credo.facelets;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.servlet.http.Part;

import org.soulwing.credo.service.FileContentModel;

/**
 * An editor that allows the user to upload files.
 *
 * @author Carl Harris
 */
@Dependent
public class FileUploadEditor implements Serializable {

  private static final long serialVersionUID = -3505712644319118706L;
  
  private final PartContent file0 = new PartContent();
  private final PartContent file1 = new PartContent();
  private final PartContent file2 = new PartContent();

  private List<FileContentModel> files;

  /**
   * Gets the {@code file0} property.
   * @return
   */
  public Part getFile0() {
    return file0.getPart();
  }

  /**
   * Sets the {@code file0} property.
   * @param file0
   */
  public void setFile0(Part part) {
    file0.setPart(part);
  }

  /**
   * Gets the {@code file1} property.
   * @return
   */
  public Part getFile1() {
    return file1.getPart();
  }

  /**
   * Sets the {@code file1} property.
   * @param file1
   */
  public void setFile1(Part part) {
    file1.setPart(part);
  }

  /**
   * Gets the {@code file2} property.
   * @return
   */
  public Part getFile2() {
    return file2.getPart();
  }

  /**
   * Sets the {@code file2} property.
   * @param file2
   */
  public void setFile2(Part part) {
    file2.setPart(part);
  }


  /**
   * Produces a list containing the files that were uploaded by the user.
   * @return list of file content models
   * @throws IOException
   */
  public List<FileContentModel> fileList() throws IOException {
    if (files == null || files.isEmpty()) {
      files = new ArrayList<FileContentModel>();
      if (file0.isLoadable()) {
        file0.load();
        files.add(file0);
      }
      if (file1.isLoadable()) {
        file1.load();
        files.add(file1);
      }
      if (file2.isLoadable()) {
        file2.load();
        files.add(file2);
      }
    }
    return files;
  }


}
