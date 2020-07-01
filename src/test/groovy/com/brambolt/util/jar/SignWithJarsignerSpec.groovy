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

import static com.brambolt.gradle.testkit.Fixtures.createFileFixture

class SignWithJarsignerSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  File jar1

  File jar2

  File keyStore

  SignWithJarsigner signer

  def setup() {
    jar1 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-1')
    jar2 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-2')
    keyStore = createFileFixture('warsigner.keystore', testProjectDir)
    signer = new SignWithJarsigner(
      'XXX',
      keyStore,
      'warsigner',
      'warsigner',
      'warsigner'
    )
  }

  def 'unsigned fails check'() {
    given:
    when:
    new CheckJar('XXX', []).apply(jar1)
    then:
    thrown IllegalStateException
  }

  def 'signed passes check'() {
    given:
    when:
    signer.apply(jar2)
    new CheckJar('XXX', []).apply(jar2)
    then:
    notThrown IllegalStateException
  }
}
