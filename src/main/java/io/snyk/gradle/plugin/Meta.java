package io.snyk.gradle.plugin;

public class Meta {

    boolean standAlone = false;
    String binary = "snyk";

    public static Meta instance = new Meta();

    public static Meta getInstance() {
        return instance;
    }

    public boolean isStandAlone() {
        return standAlone;
    }

    public void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
}
