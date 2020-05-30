package com.brambolt.util.jar;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Validates that a jar has expected signing data.
 */
class CheckSigningData {

  /**
   * The signing data file name to check for. The value <code>BRAMBOLT</code>
   * corresponds to files named <code>BRAMBOLT.RSA</code> etc.
   */
  public final String signatureFileNamePrefix;

  /**
   * Constructur.
   * @param signatureFileNamePrefix The file name prefix to check for
   */
  CheckSigningData(String signatureFileNamePrefix) {
    this.signatureFileNamePrefix = signatureFileNamePrefix;
  }

  static class Results {
    final String foundChecksums; // The .SF file
    final String foundSignatureBlockFile; // The .RSA file
    Results() { this(null, null); }
    Results(String foundChecksums, String foundSignatureBlockFile) {
      this.foundChecksums = foundChecksums;
      this.foundSignatureBlockFile = foundSignatureBlockFile;
    }
    static Results combine(Results r, Path p) {
      return r;
    }
  }

  /**
   * Checks that the parameter jar file system has the expected signing data.
   * @param fs The jar file system to check
   * @return The jar file system, if checking succeeds
   * @throws IOException If unable to carry out the checks
   */
  FileSystem apply(FileSystem fs) throws IOException {
    Results results = Files.walk(fs.getPath("META-INF"))
      .filter((Path path) -> {
        String baseName = path.getFileName().toString();
        return baseName.endsWith(".RSA") || baseName.endsWith(".SF");
      })
      .reduce(new Results(), (Results r, Path p) -> {
        String baseName = p.getFileName().toString();
        if (baseName.endsWith(signatureFileNamePrefix + ".RSA"))
          if (null == r.foundSignatureBlockFile)
            return new Results(r.foundChecksums, baseName);
          else return throwTwoSignatureBlockFiles(r, baseName, p);
        else if (baseName.endsWith(signatureFileNamePrefix + ".SF"))
          if (null == r.foundChecksums)
            return new Results(r.foundSignatureBlockFile, baseName);
          else return throwTwoChecksumFiles(r, baseName, p);
        else return throwUnexpected(baseName);
      }, (p, c) -> c);
    if (null == results.foundSignatureBlockFile ||
        results.foundSignatureBlockFile.isEmpty())
      throw new IllegalStateException(
          String.format("Missing %s.RSA", signatureFileNamePrefix));
    if (null == results.foundChecksums || results.foundChecksums.isEmpty())
      throw new IllegalStateException(
          String.format("Missing %s.SF", signatureFileNamePrefix));
    return fs;
  }

  private Results throwTwoChecksumFiles(Results results, String second, Path path) {
    return throwWhen("Found two checksum files",
        results.foundChecksums, second, path);
  }

  private Results throwTwoSignatureBlockFiles(Results results, String second, Path path) {
    return throwWhen("Found two signature block files",
        results.foundSignatureBlockFile, second, path);
  }

  private Results throwWhen(String message, String first, String second, Path path) {
    throw new IllegalStateException(
        String.format(
            "%s: %s %s in %s",
            message, first, second, path));
  }

  private Results throwUnexpected(String baseName) {
    throw new IllegalStateException(
        String.format("Unexpected signing data '%s' found", baseName));
  }
}
