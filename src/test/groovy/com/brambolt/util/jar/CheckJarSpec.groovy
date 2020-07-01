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

package com.brambolt.util.jar

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.FileSystem

import static com.brambolt.gradle.testkit.Fixtures.createFileSystemFixture
import static com.brambolt.util.jar.Attributes.ALL_PERMISSIONS

class CheckJarSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  FileSystem jar

  def setup() {
    jar = createFileSystemFixture('gradle-wrapper.jar', testProjectDir)
  }

  def 'no checks no throw'() {
    when:
    new CheckJar(null, []).apply(jar)
    then:
    notThrown Throwable
  }

  def 'missing attribute fails check'() {
    given:
    when:
    new CheckJar(null, [ ALL_PERMISSIONS ]).apply(jar)
    then:
    thrown IllegalStateException
  }

  def 'unsigned fails check when signature expected'() {
    given:
    when:
    new CheckJar('XXX', []).apply(jar)
    then:
    thrown IllegalStateException
  }
}
