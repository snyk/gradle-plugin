plugins {
  groovy
  buildsrc.convention.`kotlin-jvm`

  `kotlin-dsl`
  `java-gradle-plugin`

  buildsrc.convention.`functional-test`

  id("me.qoomon.git-versioning") version "5.2.0"
  id("com.gradle.plugin-publish") version "0.21.0"
}

group = "io.snyk.gradle.plugin"
version = "0.4"

pluginBundle {
  website = "https://github.com/snyk/gradle-plugin"
  vcsUrl = "https://github.com/snyk/gradle-plugin"
  tags = listOf("security", "scanning", "dependencies")
}

gradlePlugin {
  plugins {
    create("snykPlugin") {
      id = "io.snyk.gradle.plugin.snyk-kt"
      displayName = "Snyk Security Scanner for Gradle"
      description =
        "Find and fix vulnerabilities in you third-party dependencies with this Snyk for Gradle plugin"
      implementationClass = "io.snyk.gradle.plugin.kt.SnykPlugin"
    }
  }
}

tasks.wrapper {
  gradleVersion = "7.4.2"
  distributionType = Wrapper.DistributionType.ALL
}

dependencies {
  implementation("org.json:json:20200518")

  testImplementation("org.spockframework:spock-core:2.0-groovy-2.5")
  testImplementation("junit:junit:4.13.1")
  testImplementation("org.mockito:mockito-core:2.7.22")
}
