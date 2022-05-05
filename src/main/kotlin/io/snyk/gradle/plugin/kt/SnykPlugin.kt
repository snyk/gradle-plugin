package io.snyk.gradle.plugin.kt

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class SnykPlugin : Plugin<Project> {
  override fun apply(project: Project) {

    val extension: SnykExtension = project.buildSnykExtension()

    val snykDownloadTask = project.buildSnykDownloadTask(extension)

    project.tasks.withType<SnykTask>().configureEach {
      group = SNYK_TASK_GROUP
      dependsOn(snykDownloadTask)
      snykCli.convention(snykDownloadTask.flatMap { it.cliFile })
      arguments.convention(extension.defaultArguments)
      snykToken.convention(extension.snykToken)
    }

    project.tasks.register<SnykTask>(SNYK_MONITOR_TASK_NAME) { command.set("monitor") }
    project.tasks.register<SnykTask>(SNYK_TEST_TASK_NAME) { command.set("test") }
  }

  private fun Project.buildSnykExtension(): SnykExtension {
    return extensions.create<SnykExtension>(SNYK_EXTENSION_NAME).apply {
      snykToken.convention(
        providers.environmentVariable(SNYK_TOKEN_ENV_VAR)
          .orElse(project.providers.gradleProperty(SNYK_TOKEN_ENV_VAR))
      )

      defaultArguments.convention(emptyList())
      defaultSeverity.convention(SnykExtension.Severity.MEDIUM)

      cliAutoUpdateEnabled.convention(true)
      cliDownloadDir.convention(project.layout.projectDirectory.dir("./.gradle/snyk"))

      cliVersion.convention("v1.918.0")
      cliFilename.convention(cliFilenameConvention())
      cliSourceUri.convention(
        providers
          .zip(cliVersion, cliFilename) { version, filename -> version to filename }
          .map { (version, filename) ->
            "https://github.com/snyk/snyk/releases/download/$version/$filename"
          }
      )
    }
  }

  private fun Project.buildSnykDownloadTask(
    extension: SnykExtension
  ): TaskProvider<SnykDownloadTask> {
    return tasks.register<SnykDownloadTask>(SNYK_DOWNLOAD_TASK_NAME) {
      group = SNYK_TASK_GROUP
      description = "Download and initialise the Snyk CLI binary"

      cliVersion.convention(extension.cliVersion)
      cliFilename.convention(extension.cliFilename)
      cliSourceUri.convention(extension.cliSourceUri)

      cliChecksumAlgorithm.convention("SHA-256")
      cliChecksumFilename.convention(cliFilename.map { "$it.sha256" })

      val cliVersionAndChecksumFilename: Provider<Pair<String, String>> =
        providers.zip(cliVersion, cliChecksumFilename) { version, filename -> version to filename }

      cliChecksumSourceUri.convention(
        cliVersionAndChecksumFilename.map { (version, filename) ->
          "https://github.com/snyk/snyk/releases/download/$version/$filename"
        }
      )

      cliFilePermissions.convention("755")

      cliFile.convention(
        providers.zip(extension.cliDownloadDir, cliFilename) { dir, filename ->
          dir.file(filename)
        }
      )
    }
  }

  private fun Project.cliFilenameConvention() = providers.systemProperty("os.name")
    .map { os ->
      when {
        os.startsWith("Windows") -> "snyk-win.exe"
        os.startsWith("Mac OS")  -> "snyk-macos"
        os.startsWith("Linux")   -> "snyk-linux"
        else                     -> throw GradleException("Unsupported Operating System $os")
      }
    }

  companion object {
    const val SNYK_TASK_GROUP = "snyk"

    const val SNYK_EXTENSION_NAME = "snyk"

    const val SNYK_DOWNLOAD_TASK_NAME = "snykDownload"
    const val SNYK_MONITOR_TASK_NAME = "snykMonitor"
    const val SNYK_TEST_TASK_NAME = "snykTest"

    const val SNYK_TOKEN_ENV_VAR = "SNYK_TOKEN"
  }
}
