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
