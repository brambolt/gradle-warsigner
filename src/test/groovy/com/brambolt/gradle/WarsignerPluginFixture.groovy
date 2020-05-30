package com.brambolt.gradle

import org.junit.rules.TemporaryFolder

import static com.brambolt.gradle.testkit.Builds.createBuildFile

class WarsignerPluginFixture {

  static applyFalse(TemporaryFolder testProjectDir) {
    createBuildFile('build-no-apply.gradle', """
plugins {
  id 'com.brambolt.gradle.warsigner' apply false
}
""", testProjectDir)
  }

  static applyOnly(TemporaryFolder testProjectDir) {
    createBuildFile('build-apply-only.gradle', """
plugins {
  id 'com.brambolt.gradle.warsigner' 
}
""", testProjectDir)
  }
}
