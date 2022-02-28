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

        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
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

                // TODO: Use GCloud logging
                implementation("ch.qos.logback:logback-classic:1.2.10")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting

        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            dependencies {
                // Ktor - Kotlin Microservice framework
                // https://ktor.io/
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion")
            }
        }
    }
}


application {
    mainClass.set("ServerKt")
}


// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
                       || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}


distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}


// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}


tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}


val versionAppEngine = appVersion.replace(".","-")  // AppEngine only allows letters, numbers and hyphen
appengine {
    stage {
        setAppEngineDirectory("./")
        //setArtifact("build/libs/$jarWithDependenciesFileName")
    }
    deploy {
        projectId = "GCLOUD_CONFIG"
        version = versionAppEngine
    }
}