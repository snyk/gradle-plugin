import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea
  `kotlin-dsl`
  kotlin("jvm") version "1.6.10"
}

dependencies {
  implementation(platform(kotlin("bom")))

  implementation(kotlin("gradle-plugin"))
}

val gradleJvmTarget = "11"
val gradleKotlinTarget = "1.4"
// bump gradleKotlinTarget to 1.6 when Gradle 7.5 is released

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(gradleJvmTarget))
  }

  kotlinDslPluginOptions {
    jvmTarget.set(gradleJvmTarget)
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    languageVersion = gradleKotlinTarget
    apiVersion = gradleKotlinTarget
    jvmTarget = gradleJvmTarget
  }
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}
