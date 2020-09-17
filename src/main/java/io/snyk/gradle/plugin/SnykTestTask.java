package io.snyk.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

public class SnykTestTask extends DefaultTask {

    private Logger log = getProject().getLogger();

    String arguments;
    String severity;
    String api;

    @TaskAction
    public void doSnykTest() {
        log.debug("Snyk Test Task");
        authentication();
        setExtensionData();

        String command = "snyk test";

        if (arguments != null) {
            command += " " + arguments;
        }

        if (severity != null) {
            command += " --severity-threshold=" + severity;
        }

        Runner.Result output = Runner.runCommand(command);
        log.lifecycle(output.output);

        log.debug("severity: {}", severity);
        if (output.exitcode > 0 && severity != null) {
            throw new GradleException("Snyk Test failed");
        }
    }

    private void authentication() {
        String envToken = System.getenv("SNYK_TOKEN");
        if (envToken != null && !envToken.isEmpty()) return;

        String apiConfigToken = Runner.runCommand("snyk config get api").getOutput().trim();
        if (apiConfigToken != null && !apiConfigToken.isEmpty()) return;

        if (api == null || api.isEmpty()) {
            throw new GradleException("No API key found");
        }

        Runner.Result authResult = Runner.runCommand("snyk auth "+api);
        if (authResult.exitcode != 0) {
            throw new GradleException("snyk auth went wrong: " + authResult.getOutput());
        }
    }

    private void setExtensionData() {
        SnykExtension extension = (SnykExtension) getProject().getExtensions().findByName("snyk");
        this.arguments = extension.arguments;
        this.severity = extension.severity;
        this.api = extension.api;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
