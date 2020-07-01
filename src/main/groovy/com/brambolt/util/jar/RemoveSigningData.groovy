/*
 * Copyright 2017-2020 Brambolt ehf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brambolt.util.jar

import java.nio.file.FileSystem
import java.nio.file.Files

import static com.brambolt.nio.file.ZipFileSystems.unzip

/**
 * Removes signing data from a jar file.
 */
class RemoveSigningData {

  /**
   * Removes signing data from the parameter jar file.
   * @param file The jar file to remove signing data from
   * @return The same jar file with signing data removed
   * @throws IOException If unable to process the jar
   */
  File apply(File file) throws IOException {
    FileSystem fs = unzip(file)
    try { apply(fs) } finally { if (null != fs) fs.close() }
    return file
  }

  /**
   * Removes signing data from the parameter jar file system.
   * @param file The jar file to remove signing data from
   * @return The same jar file with signing data removed
   * @throws IOException If unable to process the jar
   */
  FileSystem apply(FileSystem fs) {
    Files.walk(fs.getPath('META-INF'))
      .filter({
        String baseName = it.fileName.toString()
        baseName.endsWith('.RSA') || baseName.endsWith('.SF')})
      .forEach({ Files.delete(it) })
    fs
  }
}

