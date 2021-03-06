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

import org.gradle.api.tasks.Input

import java.nio.file.FileSystem
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.asFile
import static com.brambolt.nio.file.ZipFileSystems.process

/**
 * This task adds attributes to the manifest of an existing jar file, by
 * opening a zip file system and replacing the manifest with an edited version.
 * If a requested attribute is already present, it not overwritten. To replace
 * attribute values, first remove and then add.
 */
class AddAttributes extends DefaultTask {

  /**
   * The jar to add attributes to. The value may be a string path, a file, a
   * path object or a closure that produces any of these.
   */
  @Input
  Object jar

  /**
   * The attributes to add.
   */
  List<String> attributes

  /**
   * Configures the task. This method is re-entrant, the task should be
   * configured at least once.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Adds the "all-permissions" manifest attribute.'
    super.configure(closure)
  }

  /**
   * The task action.
   */
  @TaskAction
  void apply() {
    if (null == attributes || attributes.isEmpty())
      return // Nothing to do
    process(asFile(jar), "Unable to add attributes") {
      FileSystem fs ->
        new com.brambolt.util.jar.AddAttributes(attributes).apply(fs) }
  }
}

