plugins {
    groovy
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0-rc-2"
}

apply(from = "$rootDir/gradle/functional-test.gradle")

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
    implementation("org.json:json:20200518")

    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.mockito:mockito-core:2.7.22")
}





