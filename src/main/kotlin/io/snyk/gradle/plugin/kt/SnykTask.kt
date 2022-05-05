package io.snyk.gradle.plugin.kt

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations

@Suppress("UnstableApiUsage") // UntrackedTask is incubating
@UntrackedTask(because = "The Snyk CLI should always execute")
abstract class SnykTask @Inject constructor(
  private val executor: ExecOperations
) : DefaultTask() {

  @get:Input
  abstract val snykToken: Property<String>

  @get:InputFile
  abstract val snykCli: RegularFileProperty

  @get:Input
  abstract val command: Property<String>

  @get:Input
  abstract val arguments: ListProperty<String>

  @get:Input
  abstract val environmentVariables: MapProperty<String, String>

  @get:Input
  @get:Optional
  abstract val severityThreshold: Property<SnykExtension.Severity?>

  @get:Input
  @get:Optional
  abstract val integrationName: Property<String?>

  @TaskAction
  fun executeSnykCommand() {
    val arguments: MutableList<String> = arguments.getOrElse(emptyList()).toMutableList()

    arguments.add(0, command.get())
    arguments.addArg("severity", severityThreshold.map { it.cliArg })
    arguments.addArg("integration-name", integrationName)

    val snykCli = snykCli.get()

    val (result, output) = ByteArrayOutputStream().use { output ->
      val result = executor.exec {
        executable = snykCli.asFile.canonicalPath

        args = arguments
        environment(environmentVariables.get())

        standardOutput = output
        errorOutput = output

        isIgnoreExitValue = true
      }
      result to output.toString().trim()
    }

    logger.quiet(
      """
        Executed Snyk, exit value:${result.exitValue}
        $output
      """.trimIndent()
    )
  }

  companion object {
    private fun MutableList<String>.addArg(
      argName: String,
      argValue: Provider<String?>,
    ) {

      val value = argValue.orNull
      if (value != null && this.none { arg -> "--$argName" in arg }) {
        this += "--$argName=${value}"
      }
    }
  }
}
