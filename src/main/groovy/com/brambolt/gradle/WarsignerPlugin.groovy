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

package com.brambolt.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This plugin implementation is provided so the plugin can be applied. It
 * does not do anything - any tasks have to be added in the build implementation.
 */
class WarsignerPlugin implements Plugin<Project> {

  /**
   * Applies the plugin to the project. Does nothing.
   * @param project The project to apply the plugin to
   */
  @Override
  void apply(Project project) {
    project.logger.debug("Applying ${getClass().getCanonicalName()}.")
  }
}
