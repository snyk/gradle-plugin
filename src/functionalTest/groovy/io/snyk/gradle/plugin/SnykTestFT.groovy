package io.snyk.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SnykTestFT extends Specification {
    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    @Rule TemporaryFolder testFolder = new TemporaryFolder()
    File buildFile
    File testFile


    def setup() {

        buildFile = testProjectDir.newFile('build.gradle')
        testFile = testFolder.newFile('build.gradle');
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

    def "can successfully scan with Snyk Test"() {

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('snyk-test')
            .withPluginClasspath()
            .build()

        then:
        println result.output
        result.output.contains("Upgrade org.zeroturnaround:zt-zip@1.12")

    }
}
