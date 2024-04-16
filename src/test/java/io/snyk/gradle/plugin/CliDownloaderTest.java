package io.snyk.gradle.plugin;

import org.gradle.api.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CliDownloaderTest {

    Logger logger = mock(Logger.class);
    CliDownloader downloader = new CliDownloader(logger);
    File snyk = new File("snyk");

    @Before
    public void init() {
        doNothing().when(logger).lifecycle(any());
        doNothing().when(logger).debug(any());
        deleteSnykBinary();

    }

    @After
    public void cleanUp() {
        deleteSnykBinary();
    }

    private void deleteSnykBinary() {
        if (snyk.exists()) {
            snyk.delete();
        }
    }

    @Test
    public void testLatestVersionNumber() throws IOException {
        String version = downloader.getLatestVersion();
        System.out.println(version);
        Version latest = Version.of(version);
        assertTrue(latest.isGreaterOrEqualThan(Version.of("1.1288.0")));
    }

    @Test
    public void downloadLatestTest() {
        assertFalse(snyk.exists());
        doNothing().when(logger).lifecycle(any());
        String version = downloader.downloadLatestVersion();
        assertNotNull(version);
        assertTrue(snyk.exists());
    }
}
