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
        setExtensionData();
        authentication();

        Runner.Result output = runSnykTest();
        log.lifecycle(output.output);

        log.debug("severity: {}", severity);
        if (output.exitcode > 0 && severity != null) {
            throw new GradleException("Snyk Test failed");
        }
    }

    private Runner.Result runSnykTest() {
        String command = "test";
        if (arguments != null) {
            command += " " + arguments;
        }

        if (severity != null) {
            command += " --severity-threshold=" + severity;
        }
        return Runner.runSnyk(command);
    }

    private void authentication() {
        log.debug("check auth");
        String envToken = System.getenv("SNYK_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            log.debug("api token found in env");
            return;
        }

        String apiConfigToken = Runner.runSnyk("config get api").getOutput().trim();
        if (apiConfigToken != null && !apiConfigToken.isEmpty()) {
            log.debug("api token found");
            return;
        }

        if (api == null || api.isEmpty()) {
            throw new GradleException("No API key found");
        }

        Runner.Result authResult = Runner.runSnyk("auth "+api);
        if (authResult.failed()) {
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
