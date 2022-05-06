package buildsrc.convention

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  `java-library`
}
dependencies {
  implementation(platform(kotlin("bom")))
}

val projectJvmTarget = "11"
val projectJvmVersion = "11"
val projectKotlinTarget = "1.6"

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(projectJvmVersion))
  }
}

tasks.withType<KotlinCompile>().configureEach {

  kotlinOptions {
    jvmTarget = projectJvmTarget
    apiVersion = projectKotlinTarget
    languageVersion = projectKotlinTarget
  }

  kotlinOptions.freeCompilerArgs += listOf(
    "-opt-in=kotlin.RequiresOptIn",
    "-opt-in=kotlin.ExperimentalStdlibApi",
    "-opt-in=kotlin.time.ExperimentalTime",
  )
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

java {
  withJavadocJar()
  withSourcesJar()
}
