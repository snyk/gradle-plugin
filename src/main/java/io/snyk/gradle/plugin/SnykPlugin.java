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
        project.getExtensions().create("snyk", SnykExtension.class);

        final Configuration config = project.getConfigurations().create("dataFiles")
                .setVisible(false)
                .setDescription("The data artifacts to be processed for this plugin.");

        config.defaultDependencies(dependencies -> dependencies.add(project.getDependencies().create("org.json:json:20200518")));


        SnykTestTask snykTestTask = project.getTasks().create("snyk-test", SnykTestTask.class);
        SnykMonitorTask snykMonitorTask = project.getTasks().create("snyk-monitor", SnykMonitorTask.class);
        SnykBinaryTask snykBinaryTaks = project.getTasks().create("snyk-check-binary", SnykBinaryTask.class);
        snykTestTask.dependsOn(snykBinaryTaks);
        snykMonitorTask.dependsOn(snykBinaryTaks);
    }
}
