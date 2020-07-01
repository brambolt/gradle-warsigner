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

package com.brambolt.util.jar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Signs jar files using a jar signer executable provided with a JDK.
 */
public class SignWithJarsigner {

  public final String signatureFileNamePrefix;

  public final File signingStore;

  public final String signingStorePassword;

  public final String signingAlias;

  public final String signingKeyPassword;

  private File javaHome;

  private File jarsigner;

  public SignWithJarsigner(
      String signatureFileNamePrefix, File signingStore,
      String signingStorePassword, String signingAlias,
      String signingKeyPassword) {
      this(signatureFileNamePrefix, signingStore, signingStorePassword,
          signingAlias, signingKeyPassword, null, null);
  }

  public SignWithJarsigner(
      String signatureFileNamePrefix, File signingStore,
      String signingStorePassword, String signingAlias,
      String signingKeyPassword, File javaHome, File jarsigner) {
      this.signatureFileNamePrefix = signatureFileNamePrefix;
      this.signingStore = signingStore;
      this.signingStorePassword = signingStorePassword;
      this.signingAlias = signingAlias;
      this.signingKeyPassword = signingKeyPassword;
  }

  public void apply(File jarFile) throws Exception {
    File signer = (null != jarsigner) ? jarsigner : findJarsigner();
    if (null == signer)
      throw new IllegalStateException("No jarsigner assigned");
    if (!signer.exists() || !signer.isFile())
      throw new IllegalStateException(
          "Not a usable jarsigner file: " + signer.getAbsolutePath());
    if (null == signatureFileNamePrefix || signatureFileNamePrefix.isEmpty())
      throw new IllegalStateException(
          "No signature file name prefix provided");
    List<String> command = Arrays.asList(
        signer.getAbsolutePath(),
        "-sigFile", signatureFileNamePrefix,
        "-keystore", signingStore.getAbsolutePath(),
        "-storepass", signingStorePassword,
        "-keypass", signingKeyPassword,
        jarFile.getAbsolutePath(),
        signingAlias);
    execute(command);
  }

  void execute(List<String> command) throws Exception {
    Process process = new ProcessBuilder().inheritIO().command(command).start();
    int exitValue = process.waitFor();
    if (0 != exitValue)
      throw new Exception(formatMessage(command));
  }

  String formatMessage(List<String> command) {
    return String.format("Jarsigner failed: %s", String.join(" ", command));
  }

  File findJarsigner() {
    File javaHome = findJavaHome();
    String jarsignerFileName = getJarsignerFileName();
    String jarsignerRelPath = "bin/" + jarsignerFileName;
    File jarsigner = new File(javaHome, jarsignerRelPath);
    if (!jarsigner.exists()) {
      // This is cheeky - but in case org.gradle.java.home got mixed up or we
      // inadvertently wound up using the JRE by some mistake, we'll peek
      // "upstairs" and grab the jarsigner there if we find it...
      jarsignerRelPath = "../bin/" + jarsignerFileName;
      jarsigner = new File(javaHome, jarsignerRelPath);
    }
    return jarsigner;
  }

  File findJavaHome() {
    return new File(System.getProperty("java.home"));
  }

  String getJarsignerFileName() {
    return isWindows() ? "jarsigner.exe" : "jarsigner";
  }

  boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }
}

