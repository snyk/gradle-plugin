package io.snyk.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;


public class SnykPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        Logger log = project.getLogger();
        log.debug("Plugin loaded");
        SnykExtension extension = project.getExtensions().create("snyk", SnykExtension.class);

        final Configuration config = project.getConfigurations().create("dataFiles")
                .setVisible(false)
                .setDescription("The data artifacts to be processed for this plugin.");

        SnykTestTask snykTestTask = project.getTasks().create("snyk-test", SnykTestTask.class, task -> addProperties(task, extension));
        SnykMonitorTask snykMonitorTask = project.getTasks().create("snyk-monitor", SnykMonitorTask.class, task -> addProperties(task, extension));
        SnykBinaryTask snykBinaryTaks = project.getTasks().create("snyk-check-binary", SnykBinaryTask.class, task -> {
            task.getSnykAutoUpdate().set(extension.autoUpdate);
            task.getSnykAutoDownload().set(extension.autoDownload);
        });
        snykTestTask.dependsOn(snykBinaryTaks);
        snykMonitorTask.dependsOn(snykBinaryTaks);
    }
    
    private void addProperties(SnykTask task, SnykExtension extension) {
		task.getSnykArguments().set(extension.arguments);
		task.getSnykSeverity().set(extension.severity);
		task.getSnykApi().set(extension.api);
	}
}
