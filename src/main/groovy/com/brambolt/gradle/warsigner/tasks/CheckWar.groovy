package com.brambolt.gradle.warsigner.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.brambolt.gradle.SpecObjects.getFile


/**
 * <p>This task validates signing data and manifest attributes of application
 * jars contained in a WAR file. If validation fails a
 * <code>GradleException</code> is thrown.</p>
 *
 * <p>This task is rarely used as-is but should be extended with an optional
 * set of signing data for comparison, so the task can also be used to verify
 * that binaries have not been tampered with. This is not implemented yet.</p>
 */
class CheckWar extends DefaultTask {

  /**
   * The signature file name to check for. The value <code>BRAMBOLT</code>
   * corresponds to files named <code>BRAMBOLT.RSA</code> etc.
   */
  String signatureFileNamePrefix = ''

  /**
   * The jar manifest attributes to check for, defaults to no checking.
   */
  List<String> attributes = []

  /**
   * The WAR file to check. The value can be a string path, a file, a path
   * object or a closure that produces any of these.
   */
  Object war

  /**
   * Configures the task. The task should be configured at least once.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    group = 'Warsigner'
    description = 'Validates application jar permissions and signing data.'
    super.configure(closure)
  }

  /**
   * The task action. Checks the configured WAR file and throws an exception
   * if any check fails.
   * @throws GradleException If any jar in the WAR file fails a check
   */
  @TaskAction
  void apply() {
    com.brambolt.util.jar.CheckWar check =
      new com.brambolt.util.jar.CheckWar(signatureFileNamePrefix, attributes)
    File warFile = getFile(war)
    check.apply(warFile)
    logger.info("Successfully checked signing data and manifest attributes in ${warFile}")
  }
}
