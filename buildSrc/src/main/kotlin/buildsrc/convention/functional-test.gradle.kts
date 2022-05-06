package buildsrc.convention

plugins {
  groovy
  `jvm-test-suite`
}

testing {
  @Suppress("UnstableApiUsage")
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnit()
    }

    val functionalTest by registering(JvmTestSuite::class) {
      testType.set(TestSuiteType.FUNCTIONAL_TEST)

      dependencies {
        implementation(project)
      }

      sources {
        project.plugins.withType<GroovyBasePlugin> {
          groovy {
            setSrcDirs(listOf("src/functTest/groovy"))
          }
        }
        project.plugins.withType<JavaBasePlugin> {
          java {
            setSrcDirs(listOf("src/functTest/java"))
          }
        }
      }

      targets {
        all {
          testTask.configure {
            shouldRunAfter(test)
          }
        }
      }
    }

    tasks.check {
      dependsOn(functionalTest)
    }
  }
}

// make 'functionalTest' use the same dependencies as 'testImplementation'
configurations.named("functionalTestImplementation").configure {
  extendsFrom(configurations.named("testImplementation").get())
}
