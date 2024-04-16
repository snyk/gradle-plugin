package io.snyk.gradle.plugin;

import org.gradle.api.GradleException;

import org.gradle.api.logging.Logger;


import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CliDownloader {

    private static final String LATEST_RELEASES_URL = "https://static.snyk.io/cli/latest/release.json";
    private static final String LATEST_RELEASE_DOWNLOAD_URL = "https://static.snyk.io/cli/v%s/%s";
    private final Logger log;

    public CliDownloader(Logger logger) {
        this.log = logger;
    }

    public String getLatestVersion() throws IOException {
        URL url = new URL(LATEST_RELEASES_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String result = reader.lines().collect(Collectors.joining("\n"));
            String version = extractVersion(result);
            return version;
        }
    }
    private String extractVersion(String json) {
        String versionKey = "\"version\": \"";
        int startIndex = json.indexOf(versionKey) + versionKey.length();
        if (startIndex == versionKey.length() - 1) {
            throw new GradleException("Latest version key not found");
        }
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            throw new GradleException("Latest version key not found");
        }
        String version = json.substring(startIndex, endIndex);
        if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
            throw new GradleException("Latest version key invalid");
        }
        return version;
    }

    private String snykWrapperFileName() {
        OSFamily os = SystemUtil.osFamily();
        switch (os) {
            case WINDOWS:
                return "snyk-win.exe";
            case MAC_OS:
                return "snyk-macos";
            case LINUX:
                return "snyk-linux";
            default:
                throw new IllegalArgumentException("Unsupported OS: " + os);
        }

    }

    public String downloadLatestVersion() {
        try {
            String latestVersion = getLatestVersion();
            String filename = snykWrapperFileName();
            String downloadURL = String.format(LATEST_RELEASE_DOWNLOAD_URL, latestVersion, filename);
            log.lifecycle("Download version {} of {}", latestVersion, filename);
            if (SystemUtil.isWindows()) {
                download(downloadURL, "snyk.exe");
            } else {
                download(downloadURL, "snyk");
            }
            return latestVersion;
        } catch (IOException e) {
            throw new GradleException("Unable to download latest snyk CLI binary", e);
        }
    }

    private void download(String url, String fileName) throws IOException {
        log.lifecycle("Downloading: {}", url);
        URL website = new URL(url);
        try(ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            log.lifecycle("Downloading finished");
            setFilePermissions(fileName);
        }
    }

    private void setFilePermissions(String fileName) throws IOException {
        log.debug("Setting file permissions");

        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions((new File(fileName)).toPath(), perms);
    }


}
