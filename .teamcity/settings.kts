import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.03"

project {
    // Use DslContext.settingsRoot as a VCS root
    val vcsRoot = DslContext.settingsRoot

    // Build configuration for the desktop application
    buildType {
        id("BuildDesktopApp")
        name = "Build Desktop Application"

        vcs {
            root(vcsRoot)
        }

        steps {
            gradle {
                name = "Build Desktop Application"
                tasks = "composeApp:packageDistributionForCurrentOS"
                useGradleWrapper = true
            }
        }

        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }

        features {
            perfmon {}
        }

        artifactRules = """
            composeApp/build/compose/binaries/main/*/org.example.project-*.* => desktop-app
        """.trimIndent()
    }

    // Build configuration for the web application
    buildType {
        id("BuildWebApp")
        name = "Build Web Application"

        vcs {
            root(vcsRoot)
        }

        steps {
            gradle {
                name = "Build Web Application"
                tasks = "composeApp:wasmJsBrowserProductionWebpack"
                useGradleWrapper = true
            }
        }

        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }

        features {
            perfmon {}
        }

        artifactRules = """
            composeApp/build/dist/wasmJs/productionExecutable => web-app
        """.trimIndent()
    }
}
