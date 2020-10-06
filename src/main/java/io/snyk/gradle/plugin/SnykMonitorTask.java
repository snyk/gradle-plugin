package io.snyk.gradle.plugin;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class SnykMonitorTask extends SnykTask {


    @TaskAction
    public void doSnykTest() {
        log.debug("Snyk Monitor Task");
        authentication();

        Runner.Result output = runSnykCommand("monitor");
        log.lifecycle(output.output);

        log.debug("severity: {}", extension.severity);
        if (output.exitcode > 0 && extension.severity != null) {
            throw new GradleException("Snyk Test failed");
        }
    }
}
