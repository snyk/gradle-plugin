package buildsrc.snyk

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

abstract class SnykTask @Inject constructor(
    private val executor: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val apiKey: Property<String>

    @get:Input
    abstract val snykCli: RegularFileProperty

    @get:Input
    abstract val command: Property<String>

    @get:Input
    abstract val arguments: ListProperty<String>

    // @get:Input
    // abstract val severityThreshold: Property<SnykExtension.Severity>

    @TaskAction
    fun executeSnykCommand() {

        val arguments: MutableList<String> = arguments.getOrElse(emptyList()).toMutableList()

        // if (severityThreshold.isPresent && arguments.none { "--severity" in it }) {
        //     arguments += "--severity=${severityThreshold.get().cliArg}"
        // }

        if (arguments.none { "--integration-name" in it }) {
            arguments += "--integration-name=GRADLE_PLUGIN"
        }

        val (result, output) = ByteArrayOutputStream().use { output ->
            val result = executor.exec {
                environment(SnykPlugin.SNYK_TOKEN_ENV_VAR, apiKey.get())
                setExecutable(snykCli)
                commandLine(command.get())
                args = arguments
                standardOutput = output
            }
            result to output.toString().trim()
        }

        result.assertNormalExitValue()

        logger.lifecycle("Snyk $output")
    }
}
