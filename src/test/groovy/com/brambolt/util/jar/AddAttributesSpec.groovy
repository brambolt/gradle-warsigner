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

import java.nio.file.FileSystem
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.brambolt.gradle.testkit.Fixtures.createFileSystemFixture
import static com.brambolt.util.jar.Attributes.ALL_PERMISSIONS
import static java.nio.file.Files.createTempFile

class AddAttributesSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  def 'adds all permissions'() {
    when:
    def result = new AddAttributes(ALL_PERMISSIONS).apply(manifest1)
    then:
    result.contains('Permissions: all-permissions')
  }

  def 'does not override'() {
    when:
    def result = new AddAttributes(ALL_PERMISSIONS).apply(manifest2)
    then:
    !result.contains(ALL_PERMISSIONS)
    result.contains('Permissions: strictly-verboten')
  }

  def 'modifies existing manifest file'() {
    given:
    def manifest = createTempFile('MANIFEST', '.MF')
    manifest.text = manifest1
    when:
    new AddAttributes(ALL_PERMISSIONS).apply(manifest)
    then:
    manifest.text.contains(ALL_PERMISSIONS)
  }

  def 'modifies existing file system'() {
    given:
    FileSystem jar = createFileSystemFixture('gradle-wrapper.jar', testProjectDir)
    when:
    new AddAttributes(ALL_PERMISSIONS).apply(jar)
    then:
    jar.getPath('META-INF/MANIFEST.MF').text.contains(ALL_PERMISSIONS)
  }

  String manifest1 = """Manifest-Version: 1.0
Codebase: *

Name: Brambolt
Build-Date: 2019-04-07 11:47:28
Build-Number: SNAPSHOT
Product-Version: 2019.04.17-SNAPSHOT"""

  String manifest2 = """Manifest-Version: 1.0
Permissions: strictly-verboten
"""
}

