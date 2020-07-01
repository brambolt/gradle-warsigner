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