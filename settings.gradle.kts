rootProject.name = "snyk-gradle-plugin"

apply(from = "./buildSrc/repositories.settings.gradle.kts")

@Suppress("UnstableApiUsage") // centralised repositories are incubating
dependencyResolutionManagement {

  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  repositories {
    mavenCentral()
  }

  pluginManagement {
    repositories {
      gradlePluginPortal()
      mavenCentral()
    }
  }
}
