package io.snyk.gradle.plugin;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class SnykCodeTestTask extends SnykTask {

    @TaskAction
    public void doSnykTest() {
        log.debug("Snyk Code Test Task");
        authentication();

        Runner.Result output = runSnykCommand("code test");
        log.lifecycle(output.output);

        log.debug("severity: {}", extension.severity);
        if (output.exitcode > 0 && extension.severity != null) {
            throw new GradleException("Snyk Code Test failed");
        }
    }
}
