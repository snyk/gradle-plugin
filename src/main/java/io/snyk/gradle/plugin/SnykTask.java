package io.snyk.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;

public class SnykTask extends DefaultTask {


    protected Logger log = getProject().getLogger();
    protected SnykExtension extension = (SnykExtension) getProject().getExtensions().findByName("snyk");

    protected Runner.Result runSnykCommand(String command) {
        if (extension.arguments != null) {
            command += " " + extension.arguments;
        }

        if (extension.severity != null) {
            command += " --severity-threshold=" + extension.severity;
        }
        return Runner.runSnyk(command);
    }

    protected void authentication() {
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

        if (extension.api == null || extension.api.isEmpty()) {
            throw new GradleException("No API key found");
        }

        Runner.Result authResult = Runner.runSnyk("auth "+ extension.api);
        if (authResult.failed()) {
            throw new GradleException("snyk auth went wrong: " + authResult.getOutput());
        }
    }
}
