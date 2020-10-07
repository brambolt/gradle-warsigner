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

package com.brambolt.gradle.warsigner.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException

/**
 * Shared implementation for signing tasks.
 */
abstract class SigningTask extends DefaultTask {

  /**
   * The file name prefix to use. The value <code>BRAMBOLT</code> corresponds
   * to signing data files named <code>BRAMBOLT.RSA</code> etc.
   */
  String signatureFileNamePrefix = ''

  /**
   * The signing key store to use. The value can be a string path, a file, a
   * path object or a closure that produces any of these.
   */
  Object signingStore

  /**
   * The signing key store password.
   */
  String signingStorePassword

  /**
   * The alias of the certificate to sign with.
   */
  String signingAlias

  /**
   * The password for the certificate to sign with.
   */
  String signingKeyPassword

  /**
   * Configures signing parameters by attempting to read the values from
   * command line arguments.
   * @see #trySigningParametersFromCli
   */
  void configureSigningParameters() {
    trySigningParametersFromCli()
  }

  /**
   * <p>Reads signing configuration from command line arguments.</p>
   *
   * <ul>
   *   <li><code>-PsigningStore=&lt;path&gt;</code> sets the key store path</li>
   *   <li><code>-PsigningStorePassword=</code> sets the key store password</li>
   *   <li><code>-PsigningAlias=</code> sets the certificate alias</li>
   *   <li><code>-PsigningKeyPassword=</code> sets the certificate password</li>
   * </ul>
   * @see #configureSigningParameters
   */
  void trySigningParametersFromCli() {
    if (null == signingStore && project.rootProject.hasProperty('signingStore'))
      signingStore = project.rootProject.signingStore
    if (null == signingStorePassword && project.rootProject.hasProperty('signingStorePassword'))
      signingStorePassword = project.rootProject.signingStorePassword
    if (null == signingAlias && project.rootProject.hasProperty('signingAlias'))
      signingAlias = project.rootProject.signingAlias
    if (null == signingKeyPassword && project.rootProject.hasProperty('signingKeyPassword'))
      signingKeyPassword = project.rootProject.signingKeyPassword
  }

  /**
   * Checks that required configuration is available.
   * @throws GradleException If any check fails
   */
  void checkConfiguration() {
    printConfiguration()
    if (null == signingStore)
      throw new GradleException('Missing signing store path; use -PsigningStore=')
    if (!signingStore.exists())
      throw new GradleException("Not a signing store path: ${signingStore}")
    if (null == signingStorePassword || signingStorePassword.isEmpty())
      throw new GradleException('Missing signing store password; use -PsigningStorePassword=')
    if (null == signingAlias || signingAlias.isEmpty())
      throw new GradleException('Missing signing alias; use -PsigningAlias')
    if (null == signingKeyPassword || signingKeyPassword.isEmpty())
      throw new GradleException('Missing signing key password: use -PsigningKeyPassword')
  }

  protected void printConfiguration() {
    project.logger.info("Signing store: ${signingStore ?: 'null'}")
    // project.logger.debug("Signing store password: ${signingStorePassword ?: 'null'}")
    project.logger.info("Signing alias: ${signingAlias ?: 'null'}")
    // project.logger.debug("Signing key password: ${signingKeyPassword ?: 'null'}")
  }

  /**
   * Locates the Java home directory. This is normally accessed using the
   * <code>java.home</code> system property but if the
   * <code>org.gradle.java.home</code> property is available it takes
   * precedence.
   * @return The Java home directory
   */
  File findJavaHome() {
    String javaHomePath = System.getProperty('java.home')
    String orgGradleJavaHomePath = System.getProperty('org.gradle.java.home')
    if (null != orgGradleJavaHomePath && !orgGradleJavaHomePath.isEmpty())
      javaHomePath = orgGradleJavaHomePath
    new File(javaHomePath)
  }
}

