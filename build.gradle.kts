@file:Suppress("UnstableApiUsage")

plugins {
    groovy
    `java-gradle-plugin`
    `jvm-test-suite`
    id("com.gradle.plugin-publish") version "1.0.0-rc-2"
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
            id = "io.snyk.gradle.plugin.snykplugin"
            displayName = "Snyk Security Scanner for Gradle"
            description = "Find and fix vulnerabilities in you third-party dependencies with this Snyk for Gradle plugin"
            implementationClass = "io.snyk.gradle.plugin.SnykPlugin"
        }
    }
}

// dependencies of this plugin
dependencies {
    implementation("org.json:json:20220320")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnit()
            dependencies {
                implementation("org.mockito:mockito-core:4.5.1")
            }
        }
        register<JvmTestSuite>("funcTest") {
            useSpock()
            dependencies {
                implementation(project.dependencies.gradleTestKit())
                implementation(project)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("funcTest"))
}
