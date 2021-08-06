package io.snyk.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.Optional;

public class SnykBinaryTask extends DefaultTask {

    private Logger log = getProject().getLogger();
    private CliDownloader cliDownloader = new CliDownloader(log);

    SnykExtension extension;

    @TaskAction
    public void checkSnykBinary() {
        log.debug("Snyk Binary Task");
        extension = (SnykExtension) getProject().getExtensions().findByName("snyk");
        Optional<String> maybeVersion = getSnykVersion();

        String version = maybeVersion.map(this::autoUpgrade)
                .orElseGet(this::downloadCli);
        log.lifecycle("Using Snyk CLI version: {}", version);
    }

    private String autoUpgrade(String version) {
        if (!extension.autoUpdate) {
            return version;
        }

        try {
            String latestVersion = cliDownloader.getLatestVersion();
            if (version.contains("(standalone)")) {
                String[] versionSplit = version.split(" ");
                Version current = Version.of(versionSplit[0]);
                Version latest = Version.of(latestVersion.substring(1,latestVersion.length()));
                if (latest.isGreaterThan(current)) {
                    log.lifecycle("auto update snyk binary: {} -> {}", current, latest);
                    version = cliDownloader.downloadLatestVersion();
                }
            }
            return version;
        } catch (IOException e) {
            throw new GradleException("Unable to upgrade", e);
        }
    }

    private String downloadCli() {
        if (extension.autoDownload) {
            return cliDownloader.downloadLatestVersion();
        }
        throw new GradleException("No Snyk binary found, set autoDownload to true to download the binary");
    }

    private Optional<String> getSnykVersion() {
        Runner.Result versionResult = Runner.runCommand("snyk -version");
        if (versionResult.failed()) {
            Meta.getInstance().setStandAlone(true);
            log.lifecycle("look for standalone binary");
            if (SystemUtil.isWindows()) {
                versionResult = Runner.runCommand("snyk.exe -version");
                Meta.getInstance().setBinary("snyk.exe");
            } else {
                versionResult = Runner.runCommand("./snyk -version");
                Meta.getInstance().setBinary("./snyk");
            }
            if (versionResult.failed()) {
                log.lifecycle("no snyk standalone found");
                return Optional.empty();
            }
        } else {
            Meta.getInstance().setStandAlone(false);
            Meta.getInstance().setBinary("snyk");
        }

        return Optional.of(versionResult.output.trim());
    }
}
