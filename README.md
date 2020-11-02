![Snyk logo](https://snyk.io/style/asset/logo/snyk-print.svg)

# Snyk plugin for Gradle

[![Application CI](https://github.com/snyk/gradle-plugin/workflows/Application%20CI/badge.svg?branch=master)](https://github.com/snyk/gradle-plugin/actions?query=workflow%3A%22Application+CI%22)

Snyk helps you find, fix and monitor for known vulnerabilities in your dependencies, both on an ad hoc basis and as part of your CI (Build) system.

The Snyk Gradle plugin tests and monitors your Gradle dependencies.

| :information_source: This product is not an official Snyk supported product. It is an open-source community driven project that is initialised and partially maintained by Snyk engineers |
| --- |

## Using the Snyk Plugin for Gradle
The latest version of the plugin is released at the [Gradle Plugins Portal](https://plugins.gradle.org/plugin/io.snyk.gradle.plugin.snykplugin).
Import the plugin using the plugin DSL

Groovy:
```groovy
plugins {
  id "io.snyk.gradle.plugin.snykplugin" version "0.4"
}
```

Kotlin
```kotlin
plugins {
  id("io.snyk.gradle.plugin.snykplugin") version "0.4"
}
```

### Setting:

Groovy:
```groovy
snyk {
    arguments = '--all-sub-projects'
    severity = 'low'
    api = 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'
    autoDownload = true
    autoUpdate = true
}
```
all fields are optional

- **arguments** - add extra arguments to the Snyk CLI. See Snyk CLI help for more information. In this example it scans all sub-projects for gradle
- **severity** - what is the severity threshold. Leave empty to only show the vulnerabilites but not break 
- **api** - api key that can be found on the settings page of your (free) snyk account. Alternatively you can set a environment variable SNYK_TOKEN and ommit it here
- **autoDownload** - automatically download the CLI is none is installed (default = true)
- **autoUpdate** - update the CLI if there is a newer version (only if downloaded by gradle plugin) (default = false)

### Running:

Snyk Test:
```bash
$ gradle snyk-test
```

Snyk Test together with a clean build:
```bash
$ gradle clean build snyk-test
```

Snyk Monitor:
Snyk Test:
```bash
$ gradle snyk-monitor
```

Snyk Monitor together with a clean build:
```bash
$ gradle clean build snyk-monitor
```

