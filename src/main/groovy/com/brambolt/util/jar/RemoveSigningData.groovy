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

