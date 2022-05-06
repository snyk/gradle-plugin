// shared repository configuration, for both the buildSrc and root projects.

@Suppress("UnstableApiUsage") // centralised repositories are incubating
dependencyResolutionManagement {

  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  pluginManagement {
    repositories {
      gradlePluginPortal()
      mavenCentral()
    }
  }
}
