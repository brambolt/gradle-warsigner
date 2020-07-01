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

import com.brambolt.util.jar.SignWithJarsigner
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.asFile

/**
 * Sians a jar file.
 */
class SignJar extends SigningTask {

  /**
   * The jar to sign. The value can be a string path, a file, a path object or
   * a closure that produces any of these.
   */
  Object jar

  /**
   * The jar signer executable to use. The value can be a string path, a file,
   * a path object or a closure that produces any of these.
   */
  Object jarsigner

  /**
   * Configures the task. The task should be configured at least once.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Signs a jar.'
    super.configure(closure)
    onlyIf {
      null != signingStore && !signingStore.isEmpty()
    }
    this
  }

  /**
   * The task action. Signs the configured jar.
   * @throws GradleException If signing fails
   */
  @TaskAction
  void apply() {
    checkConfiguration()
    File jarFile = asFile(jar)
    project.logger.info("Signing ${jar.absolutePath}")
    new SignWithJarsigner(
      signatureFileNamePrefix,
      asFile(signingStore),
      signingStorePassword,
      signingKeyPassword,
      signingAlias,
      findJavaHome(),
      null).apply(jarFile)
  }
}

