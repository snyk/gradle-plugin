package io.snyk.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class SnykMonitorFT extends Specification {
    @TempDir File testProjectDir
    @TempDir File testFolder
    File buildFile
    File testFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        testFile = new File(testFolder, 'build.gradle');
        def testFileString = testFile.getAbsolutePath();

        buildFile << """
            plugins {
                id 'java'
                id 'io.snyk.gradle.plugin.snykplugin'
            }

            repositories{
                mavenCentral()
            }

            snyk {
                arguments = '--file=$testFileString'
            }

        """

        testFile << """
            plugins {
                id 'java'
            }

            repositories{
                mavenCentral()
            }

            dependencies {
                // This dependency is found on compile classpath of this component and consumers.
                compile 'com.google.guava:guava:27.0.1-jre'
                compile 'org.zeroturnaround:zt-zip:1.12'
            }
        """
    }

    def "can successfully monitored with Snyk Monitor"() {
        when:
        def result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir)
            .withArguments('snyk-monitor')
            .build()

        then:
        println result.output
        result.output.contains("Explore this snapshot at ")
        result.output.contains("BUILD SUCCESSFUL")
    }
}
