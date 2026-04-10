package io.snyk.gradle.plugin;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public abstract class SnykTestTask extends SnykTask {

    @TaskAction
    public void doSnykTest() {
        log.debug("Snyk Test Task");
        authentication();

        Runner.Result output = runSnykCommand("test");
        log.lifecycle(output.output);

        log.debug("severity: {}", getSnykSeverity().getOrNull());
        if (output.exitcode > 0 && getSnykSeverity().getOrNull() != null) {
            throw new GradleException("Snyk Test failed");
        }
    }
}
