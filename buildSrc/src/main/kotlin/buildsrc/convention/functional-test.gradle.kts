package buildsrc.convention

plugins {
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
        java {
          setSrcDirs(listOf("src/functTest"))
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

configurations.named("functionalTestImplementation").configure {
  extendsFrom(configurations.named("testImplementation").get())
}
