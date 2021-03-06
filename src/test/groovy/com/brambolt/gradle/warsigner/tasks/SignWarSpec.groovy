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

import com.brambolt.util.jar.CheckWar
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.brambolt.gradle.testkit.Fixtures.createFileFixture
import static com.brambolt.util.jar.Attributes.ALL_PERMISSIONS

class SignWarSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  File war

  File keyStore

  SignWar task

  def setup() {
    war = createFileFixture('fixture.war', testProjectDir)
    keyStore = createFileFixture('warsigner.keystore', testProjectDir)

  }

  def 'fixture is unsigned'() {
    when:
    new CheckWar('XXX').apply(war)
    then:
    def exception = thrown(IllegalStateException)
    exception.message == 'Missing XXX.RSA'
  }

  def 'fixture is missing permissions'() {
    when:
    new CheckWar([ALL_PERMISSIONS]).apply(war)
    then:
    def exception = thrown(IllegalStateException)
    exception.message == "Missing attribute: ${ALL_PERMISSIONS}"
  }

  def 'can sign'() {
    given:
    task = ProjectBuilder.builder().build().task(type: SignWar, 'sign') as SignWar
    task.signatureFileNamePrefix = 'XXX'
    task.signingAlias = 'warsigner'
    task.signingKeyPassword = 'warsigner'
    task.signingStore = keyStore
    task.signingStorePassword = 'warsigner'
    task.war = war
    task.sign = true
    task.unsign = true
    task.attributes = [ ALL_PERMISSIONS ]
    task.configure({})
    when:
    task.apply()
    new CheckWar(task.signatureFileNamePrefix, [ALL_PERMISSIONS]).apply(war)
    then:
    noExceptionThrown()
  }
}
