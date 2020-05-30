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
