package io.snyk.gradle.plugin.kt

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class SnykExtension {
  abstract val apiKey: Property<String>

  abstract val defaultArguments: ListProperty<String>
  abstract val defaultSeverity: Property<Severity>

  abstract val cliAutoUpdateEnabled: Property<Boolean>
  abstract val cliDownloadDir: DirectoryProperty

  abstract val cliVersion: Property<String>
  abstract val cliFilename: Property<String>
  abstract val cliSourceUri: Property<String>

  enum class Severity(
    val cliArg: String
  ) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    CRITICAL("critical"),
  }
}
