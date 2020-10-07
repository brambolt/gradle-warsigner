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

import com.brambolt.util.jar.AddAttributes
import com.brambolt.util.jar.RemoveSigningData
import com.brambolt.util.jar.SignWithJarsigner
import java.nio.file.Files
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path

import static com.brambolt.nio.file.ZipFileSystems.unzip
import static com.brambolt.gradle.SpecObjects.asFile

/**
 * <p>Signs a WAR file. This task can remove existing signing data and add jar
 * manifest attributes to application jars in the configured WAR file, before
 * signing the jars with the provided certificate.</p>
 *
 * <p>The most common use case is to sign WAR files before deployment to
 * production, or resign in case of expired certificate.</p>
 *
 * <p>A second common use case is to resign webstart WAR files to ensure that
 * all jars are signed with the same certificate and at the same time make
 * sure that the <code>Permissions: all-permissions</code> attributed required
 * with Java 8 and onwards is always set.</p>
 */
class SignWar extends SigningTask {

  /**
   * Indicates whether signing data should be removed before signing.
   */
  Boolean unsign = true

  /**
   * Lists jar manifest attributes to be added. Empty by default.
   */
  List<String> attributes = []

  /**
   * Indicates whether to sign application jar files in the WAR file.
   */
  Boolean sign = true

  /**
   * The WAR file to process. The value can be a string path, a file, a path
   * object, or a closure that produces any of these.
   */
  Object war

  /**
   * The jar signer implementation used for signing.
   */
  SignWithJarsigner signer

  /**
   * The signing data cleaner used for removing existing signing data.
   */
  RemoveSigningData cleaner

  /**
   * The add-attributes implementation.
   */
  AddAttributes adder

  /**
   * Configures the task. The task should be configured at least once, to
   * make sure defaults are established.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Repairs permissions and resigns jars in a WAR.'
    super.configure(closure)
    configureSigningParameters()
    onlyIf { shouldExecute() }
    configureDefaults()
    onlyIf { sign || unsign || (null != attributes && !attributes.isEmpty()) }
    doFirst {
      if (unsign)
        project.logger.quiet('Removing pre-existing signing data...')
      if (!attributes.isEmpty()) {
        project.logger.quiet('Granting permissions and resigning jars...')
        project.logger.info("Adding attributes:\n\t${attributes.join('\n\t')}")
      }
      if (sign)
        project.logger.quiet('Signing jars...')
    }
    this
  }

  boolean shouldExecute() {
    null != signingStore && (
      (signingStore instanceof String && !signingStore.trim().isEmpty()) ||
      (signingStore instanceof File))
  }

  /**
   * Configures defaults.
   */
  void configureDefaults() {
    configureJarsigner()
    configureCleaner()
    configureAdder()
  }

  /**
   * Initializes the jar signer implementation if an implementation was not
   * provided during configuration. Subsequent invocations do nothing unless
   * the value is explicitly cleared.
   */
  void configureJarsigner() {
    if (null == signer)
      signer = new SignWithJarsigner(
        signatureFileNamePrefix,
        asFile(signingStore),
        signingStorePassword,
        signingAlias,
        signingKeyPassword)
  }

  /**
   * Initializes the attribute cleaner if an implementation was not provided
   * during configuration. Subsequent invocations do nothing unless the value
   * is explicitly cleared.
   */
  void configureCleaner() {
    if (null == cleaner)
      cleaner = new RemoveSigningData()
  }

  /**
   * Initializes the attribute adder if an implementation was not provided
   * during configuration. Subsequent invocations do nothing unless the value
   * is explicitly cleared.
   */
  void configureAdder() {
    if (null == adder)
      adder = new AddAttributes(attributes)
  }

  /**
   * The task action.
   */
  @TaskAction
  void apply() {
    // If no client-specific signing store is provided, do nothing:
    // If the signing store is provided, we will sign; but we need
    // the remaining configuration parameters:
    checkConfiguration()
    // Clean signing data, set permissions and resign each application jar:
    File warFile = asFile(war)
    Path tmpDir = Files.createTempDirectory(warFile.getName())
    ant.unzip(src: warFile, dest: tmpDir)
    apply(tmpDir.toFile())
    ant.delete(file: warFile)
    ant.zip(basedir: tmpDir, destfile: warFile)
    ant.delete(dir: tmpDir)
    logger.info("Fixed signing data and permission attributes in ${warFile.absolutePath}")
  }

  protected File apply(File dir) {
    Files.walk(new File(dir, "application-jars").toPath())
      .filter({ it.toString().endsWith('.jar') })
      .forEach({ Path jarPath -> applyToJar(jarPath) })
    dir
  }

  protected void applyToJar(Path jarPath) {
    if (unsign || (null != attributes && !attributes.isEmpty()))
      unzip(jarPath).with { jarFs ->
        if (null != cleaner) cleaner.apply(jarFs)
        if (null != adder) adder.apply(jarFs)
        jarFs.close()
      }
    if (null != signer) signer.apply(jarPath.toFile())
  }
}

