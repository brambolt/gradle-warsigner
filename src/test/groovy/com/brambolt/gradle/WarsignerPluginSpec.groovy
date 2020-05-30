package com.brambolt.gradle

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.brambolt.gradle.testkit.Builds.runTask
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class WarsignerPluginSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  Map buildFiles

  def setup() {
    buildFiles = [
      applyFalse: WarsignerPluginFixture.applyFalse(testProjectDir),
      applyOnly: WarsignerPluginFixture.applyOnly(testProjectDir)
    ]
  }

  def 'can include plugin without applying'() {
    when:
    def result = runTask(testProjectDir.root,
      '-b', buildFiles.applyFalse.name as String,
      'tasks', '--debug', '--stacktrace')
    then:
    result.task(":tasks").outcome == SUCCESS
  }

  def 'can apply plugin'() {
    when:
    def result = runTask(testProjectDir.root,
      '-b', buildFiles.applyOnly.name as String, 'tasks')
    then:
    result.task(":tasks").outcome == SUCCESS
  }
}