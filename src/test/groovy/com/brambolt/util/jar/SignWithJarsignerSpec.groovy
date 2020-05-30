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
