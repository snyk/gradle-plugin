package io.snyk.gradle.plugin.kt

import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class SnykDownloadTask @Inject constructor(
  private val fileOp: FileSystemOperations,
) : DefaultTask() {

  @get:Input
  abstract val cliVersion: Property<String>
  @get:Input
  abstract val cliFilename: Property<String>
  @get:Input
  abstract val cliSourceUri: Property<String>

  @get:Input
  abstract val cliChecksumFilename: Property<String>
  @get:Input
  abstract val cliChecksumAlgorithm: Property<String>
  /** The checksum to validate - set to `null` to disable checksum validation */
  @get:Input
  abstract val cliChecksumSourceUri: Property<String?>

  /**
   * Set the permissions of [cliFile], using [Linux octal permissions](https://en.wikipedia.org/wiki/File-system_permissions#Numeric_notation).
   *
   * @see[org.gradle.api.file.CopySpec.setFileMode]
   */
  @get:Input
  abstract val cliFilePermissions: Property<String>

  /** The location where the Snyk CLI will be saved. */
  @get:OutputFile
  abstract val cliFile: RegularFileProperty

  @TaskAction
  fun exec() {
    val cliName = cliFile.get().asFile.name

    val cliDestination = temporaryDir.resolve(cliName)
    downloadFile(cliSourceUri.get(), cliDestination)

    validateCliChecksum(cliDestination)

    fileOp.sync {
      from(cliDestination)
      into(cliFile.get().asFile.parentFile)
      // convert from octal to decimal
      fileMode = cliFilePermissions.get().toInt(8)
    }
  }

  private fun downloadFile(
    source: String,
    destination: File,
  ) {
    ant.invokeMethod(
      "get",
      mapOf(
        "src" to source,
        "dest" to destination.canonicalPath,
        "verbose" to true,
      )
    )
  }

  private fun validateCliChecksum(cliDestination: File) {
    val cliChecksumSourceUri = cliChecksumSourceUri.orNull
    if (cliChecksumSourceUri != null) {
      val cliChecksumAlgorithm = cliChecksumAlgorithm.get()
      val cliChecksumFilename = cliChecksumFilename.get()

      val cliChecksumDestination = temporaryDir.resolve(cliChecksumFilename)
      downloadFile(cliChecksumSourceUri, cliChecksumDestination)

      val expectedCliChecksum = cliChecksumDestination.readText().takeWhile { !it.isWhitespace() }

      val actualCliChecksum = MessageDigest.getInstance(cliChecksumAlgorithm)
        .digest(cliDestination.readBytes())
        .fold("") { str, it -> str + "%02x".format(it) }

      require(actualCliChecksum == expectedCliChecksum) {
        """
            Invalid $cliChecksumAlgorithm checksum for $cliDestination
            Expected: $expectedCliChecksum
            Actual: $actualCliChecksum
        """.trimIndent()
      }
    } else {
      logger.lifecycle("Checksum validation for Snyk CLI is disabled")
    }
  }
}
