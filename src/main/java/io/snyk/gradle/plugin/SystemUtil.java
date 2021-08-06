package io.snyk.gradle.plugin;

import static io.snyk.gradle.plugin.OSFamily.*;


public class SystemUtil {

    private SystemUtil() {}

    public static boolean isWindows() {
        return osFamily() == WINDOWS;
    }

    //copied logic from internal gradle class org.gradle.internal.os.OperatingSystem.java
    public static OSFamily osFamily() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return WINDOWS;
        } else if (osName.contains("mac os x") || osName.contains("darwin") || osName.contains("osx")) {
            return MAC_OS;
        } else if (osName.contains("sunos") || osName.contains("solaris")) {
            return SOLARIS;
        } else if (osName.contains("linux") || osName.contains("freebsd")) {
            return LINUX;
        } else {
            // Not strictly true
            return UNIX;
        }
    }

}


