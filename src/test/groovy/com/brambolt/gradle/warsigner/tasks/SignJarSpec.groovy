package com.brambolt.gradle.warsigner.tasks

import com.brambolt.util.jar.CheckJar
import com.brambolt.util.jar.RemoveSigningData
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.brambolt.gradle.testkit.Fixtures.createFileFixture

class SignJarSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  File jar1

  File jar2

  File jar3

  File keyStore

  SignJar task

  def setup() {
    jar1 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-1')
    jar2 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-2')
    jar3 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-3')
    keyStore = createFileFixture('warsigner.keystore', testProjectDir)
    task = ProjectBuilder.builder().build().task(type: SignJar, 'sign') as SignJar
    task.signingAlias = 'warsigner'
    task.signingKeyPassword = 'warsigner'
    task.signingStore = keyStore
    task.signingStorePassword = 'warsigner'  }

  def 'can sign'() {
    given:
    task.signatureFileNamePrefix = 'XXX'
    task.jar = jar1
    when:
    task.apply()
    new CheckJar('XXX').apply(jar1)
    then:
    notThrown Throwable
  }

  def 'must remove signing data before resigning'() {
    given:
    task.jar = jar2
    when:
    task.signatureFileNamePrefix = 'XXX'
    task.apply()
    new CheckJar('XXX').apply(jar2)
    task.signatureFileNamePrefix = 'YYY'
    task.apply()
    new CheckJar('YYY').apply(jar2)
    then:
    thrown IllegalStateException
  }

  def 'can clean and resign'() {
    given:
    task.jar = jar3
    when:
    task.signatureFileNamePrefix = 'XXX'
    task.apply()
    new CheckJar('XXX').apply(jar3)
    new RemoveSigningData().apply(jar3)
    task.signatureFileNamePrefix = 'YYY'
    task.apply()
    new CheckJar('YYY').apply(jar3)
    then:
    notThrown IllegalStateException
  }
}
