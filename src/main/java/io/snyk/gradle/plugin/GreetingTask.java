package io.snyk.gradle.plugin;


import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.json.JSONObject;


public class GreetingTask extends DefaultTask {

    Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    @TaskAction
    public void doHelloWorld() {
        System.out.println(project.getName());
        project.getConfigurations().getByName("compile")
                .getResolvedConfiguration()
                .getFirstLevelModuleDependencies()
                .stream().map(this::marshallModule)
                .forEach(json -> System.out.println(json.toString(4)));
        System.out.println("Hello World!!!!!!!!!!!!!!!!");
    }

//    private String marshallModule(ResolvedDependency resolvedDependency) {
//        System.out.println(StringUtils.chop("Brian"));
//        JSONObject jo = new JSONObject();
//        jo.put("name", "jon doe");
//        jo.put("age", "22");
//        jo.put("city", "chicago");
//        return jo.toString();
//    }

    private JSONObject marshallModule(ResolvedDependency dep) {
//        JSONObject jo = new JSONObject();
//        jo.put("name", "jon doe");
//        jo.put("age", "22");
//        jo.put("city", "chicago");
//        return jo;
        ModuleVersionIdentifier module = dep.getModule().getId();
        JSONObject json = new JSONObject();
        json.put("package", module.getGroup() + ":" + module.getName());
        json.put("version", module.getVersion());
        JSONObject dependencies = new JSONObject();
        dep.getChildren().forEach(transDep -> {
            ModuleVersionIdentifier depModule = transDep.getModule().getId();
            dependencies.put(depModule.getGroup() + ":" + depModule.getName(), marshallModule(transDep));
        });
        json.put("dependencies", dependencies);
        return json;
    }
}
