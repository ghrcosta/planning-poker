import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

// See gradle.properties
val appVersion: String by project

group = "com.ghrcosta"
version = appVersion


plugins {
    val kotlinVersion = "1.6.10"

    kotlin("multiplatform") version kotlinVersion

    /** Common plugins **/

    // For conversion between Kotlin object and JSON representation
    kotlin("plugin.serialization") version kotlinVersion


    /** JVM-specific plugins **/

    // Required for the JVM to run
    application

    // Adds empty constructors to Kotlin data classes, required to insert/retrieve data to/from database.
    // https://kotlinlang.org/docs/no-arg-plugin.html
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion  // See section "noArg" below


    /** Deploy-related plugins **/

    // Creates jar containing all project dependencies
    // https://ktor.io/docs/fatjar.html#prerequisites
    // https://github.com/johnrengelman/shadow
    id("com.github.johnrengelman.shadow") version "7.1.0"

    // Plugin for AppEngine deployment. The "-appyaml" part is not required but is recommended.
    // https://github.com/GoogleCloudPlatform/app-gradle-plugin
    // https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#applying-the-plugin
    id("com.google.cloud.tools.appengine-appyaml") version "2.4.2"
}

noArg {
    annotation("NoArgConstructor")
}


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()
    }
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    sourceSets {
        val ktorVersion = "1.6.7"
        val reactVersion = "17.0.2-pre.299-kotlin-1.6.10"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                // Ktor - Kotlin Microservice framework
                // https://ktor.io/
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")

                // Google Cloud Firebase - Firestore database
                // https://firebase.google.com/support/release-notes/admin/java
                implementation("com.google.firebase:firebase-admin:8.1.0")

                // Google Cloud Logging via Logback appender
                // https://cloud.google.com/logging/docs/setup/java
                implementation("com.google.cloud:google-cloud-logging-logback:0.123.4-alpha")
            }
        }

        val jvmTest by getting

        val jsMain by getting {
            dependencies {
                // Ktor - Kotlin Microservice framework
                // https://ktor.io/
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-css:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion")

                implementation(npm("firebase", "9.4.1"))

                // Material UI (MUI) - Ready-to-use UI components
                // MIT License
                // https://mui.com/pt/core/
                implementation(npm("@mui/material", "5.4.4"))
                implementation(npm("@emotion/react", "11.8.1"))
                implementation(npm("@emotion/styled", "11.8.1"))
            }
        }
    }
}


application {
    // Sets the initialization class. Used both to execute locally (run task) and to generate the jar (shadowJar task).
    mainClass.set("ServerKt")
}


tasks.getByName<Jar>("jvmJar") {
    val commandIsShadowJar = project.gradle.startParameter.taskNames.contains("shadowJar")
    val commandIsAppengineDeploy = project.gradle.startParameter.taskNames.contains("appengineDeploy")
    val taskName =
        if (commandIsShadowJar || commandIsAppengineDeploy) {
            "jsBrowserProductionWebpack"
        } else {
            "jsBrowserDevelopmentWebpack"
        }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}


tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}


/** Name of the jar file containing all dependencies that must be deployed and executed by GCP.  */
val jarWithDependenciesFileName = "${project.name}-$version-with-dependencies.jar"

tasks {
    // Configure task that will create the jar with dependencies
    val shadowJarTask = named<ShadowJar>("shadowJar") {
        // Execute task to create application jar. It was also configured to compile browser .js file (see above).
        dependsOn(getByName<Jar>("jvmJar"))

        // For some reason the .js file is not included in the jar by default, so add it manually.
        // At the same time, remove the minification mapping file since we don't need to distribute it.
        from("$buildDir/distributions/").exclude("*.map")

        // Required by Firestore - https://stackoverflow.com/a/63474092
        mergeServiceFiles()

        archiveFileName.set(jarWithDependenciesFileName)
    }

    // Ensure the "assemble" task (executed by gcloud during deploy) will build the correct jar file
    named("assemble") {
        dependsOn(shadowJarTask)
    }

    // We don't need .zip/.tar file distributions
    named("distZip") {
        enabled = false
    }
    named("distTar") {
        enabled = false
    }
}


val versionAppEngine = "kotlin-multiplatform-${appVersion.replace(".","-")}"  // AppEngine only allows letters, numbers and hyphen
appengine {
    stage {
        setAppEngineDirectory("./")
        setArtifact("build/libs/$jarWithDependenciesFileName")
    }
    deploy {
        projectId = "GCLOUD_CONFIG"
        version = versionAppEngine
    }
}