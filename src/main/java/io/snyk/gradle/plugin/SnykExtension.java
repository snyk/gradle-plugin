package io.snyk.gradle.plugin;

import org.gradle.api.GradleException;

import java.util.Arrays;

public class SnykExtension {

    String arguments;
    String api;
    String severity;
    boolean autoDownload = true;
    boolean autoUpdate = false;

    private static final String[] severities = new String[]{"high", "medium", "low"};

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setSeverity(String severity) {
        if (Arrays.stream(severities).anyMatch(severity::equals)) {
            this.severity = severity;
        } else {
            throw new GradleException("Severity should either high|medium|low or empty");
        }
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
}
