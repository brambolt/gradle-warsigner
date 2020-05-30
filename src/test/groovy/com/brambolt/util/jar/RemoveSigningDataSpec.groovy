package com.brambolt.util.jar

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.brambolt.gradle.testkit.Fixtures.createFileFixture

class RemoveSigningDataSpec extends Specification {

  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

  File jar1

  File jar2

  File keyStore

  SignWithJarsigner signer

  RemoveSigningData remover

  def setup() {
    jar1 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-1')
    jar2 = createFileFixture('gradle-wrapper.jar', testProjectDir, '-2')
    keyStore = createFileFixture('warsigner.keystore', testProjectDir)
    remover = new RemoveSigningData()
    signer = new SignWithJarsigner(
      'XXX',
      keyStore,
      'warsigner',
      'warsigner',
      'warsigner'
    )
  }

  def 'can clean unsigned'() {
    given:
    when:
    remover.apply(jar1)
    then:
    notThrown IllegalStateException
  }

  def 'cleaned fails check'() {
    given:
    when:
    signer.apply(jar2)
    remover.apply(jar2)
    new CheckJar('XXX', []).apply(jar2)
    then:
    thrown IllegalStateException
  }
}
