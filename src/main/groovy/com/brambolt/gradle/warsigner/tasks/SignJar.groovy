package com.brambolt.gradle.warsigner.tasks

import com.brambolt.util.jar.SignWithJarsigner
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.asFile

/**
 * Sians a jar file.
 */
class SignJar extends SigningTask {

  /**
   * The jar to sign. The value can be a string path, a file, a path object or
   * a closure that produces any of these.
   */
  Object jar

  /**
   * The jar signer executable to use. The value can be a string path, a file,
   * a path object or a closure that produces any of these.
   */
  Object jarsigner

  /**
   * Configures the task. The task should be configured at least once.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Signs a jar.'
    super.configure(closure)
    onlyIf {
      null != signingStore && !signingStore.isEmpty()
    }
    this
  }

  /**
   * The task action. Signs the configured jar.
   * @throws GradleException If signing fails
   */
  @TaskAction
  void apply() {
    checkConfiguration()
    File jarFile = asFile(jar)
    project.logger.info("Signing ${jar.absolutePath}")
    new SignWithJarsigner(
      signatureFileNamePrefix,
      asFile(signingStore),
      signingStorePassword,
      signingKeyPassword,
      signingAlias,
      findJavaHome(),
      null).apply(jarFile)
  }
}

