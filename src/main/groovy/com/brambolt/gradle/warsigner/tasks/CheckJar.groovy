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

import java.nio.file.FileSystem
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.asFile
import static com.brambolt.nio.file.ZipFileSystems.process

/**
 * <p>This task validates jar signing data and manifest attributes. If
 * validation fails a <code>GradleException</code> is thrown.</p>
 *
 * <p>This task is rarely used as-is but should be extended with an optional
 * set of signing data for comparison, so the task can also be used to verify
 * that binaries have not been tampered with. This is not implemented yet.</p>
 */
class CheckJar extends DefaultTask {

  /**
   * The basename of the signature files. The value <code>BRAMBOLT</code>
   * corresponds to <code>BRAMBOLT.RSA</code> etc.
   */
  String signatureFileNamePrefix = ''

  /**
   * The jar to check. The value may be a string path, a file, a path object or
   * a closure producing any of these.
   */
  Object jar

  /**
   * The jar manifest attributes to check for. The default is not to check for
   * any attributes.
   */
  List<String> attributes = []

  /**
   * Configures the task. The task should be configured at least once.
   * @param closure The configuration closure
   * @return The task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Validates jar manifest attributes and signing data.'
    super.configure(closure)
  }

  /**
   * The task action. Checks the configured <code>jar</code>.
   */
  @TaskAction
  void apply() {
    apply(asFile(jar))
  }

  /**
   * Checks the parameter jar file.
   * @param jarFile The jar file to check
   */
  void apply(File jarFile) {
    process(jarFile, "Unable to check ${jarFile}", ({ FileSystem fs ->
      new com.brambolt.util.jar.CheckJar(
        signatureFileNamePrefix, attributes).apply(fs)
    } as Closure<Void>))
  }
}
