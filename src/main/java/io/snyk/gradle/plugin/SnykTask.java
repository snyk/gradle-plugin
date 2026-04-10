package io.snyk.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public abstract class SnykTask extends DefaultTask {


    protected Logger log = getLogger();
    
    @Input
    @Optional
    protected abstract Property<String> getSnykArguments();
    
    @Input
    @Optional
    protected abstract Property<String> getSnykSeverity();
    
    @Input
    @Optional
    protected abstract Property<String> getSnykApi();

    protected Runner.Result runSnykCommand(String command) {
        if (getSnykArguments().getOrNull() != null) {
            command += " " + getSnykArguments().get();
        }

        if (getSnykSeverity().getOrNull() != null) {
            command += " --severity-threshold=" + getSnykSeverity().get();
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

        if (getSnykApi().getOrNull() == null || getSnykApi().get().isEmpty()) {
            throw new GradleException("No API key found");
        }

        Runner.Result authResult = Runner.runSnyk("auth "+ getSnykApi().get());
        if (authResult.failed()) {
            throw new GradleException("snyk auth went wrong: " + authResult.getOutput());
        }
    }
}
