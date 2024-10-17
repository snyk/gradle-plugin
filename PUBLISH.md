# Publish  Instruction

Instruction to publish plugin to gradle plugin portal

using legacy publish plugin

full instructions here: https://plugins.gradle.org/docs/publish-plugin-legacy

## publish setting
make sure properties are set in
__$USER_HOME/.gradle/gradle.properties__

- gradle.publish.key
- gradle.publish.secret

## Run publish plugin

```shell
./gradlew publishPlugins
```