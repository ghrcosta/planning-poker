import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.9.0"

	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion

	// Adds empty constructors to Kotlin data classes, required to insert/retrieve data to/from database.
	// https://kotlinlang.org/docs/no-arg-plugin.html
	id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion  // See section "noArg" below

	// Plugin for AppEngine deployment. The "-appyaml" part is not required but recommended.
	// Note that in order for this plugin to work, there's extra configuration in the settings.gradle from the root
	// project. The documentation (links below) offer another way of adding the plugin, via the old "buildscript{}"
	// block. However, plugins added that way are compiled differently from those in this "plugin{}" block -- the main
	// difference is that their methods aren't pre-compiled, so calls such as "appengine{}" aren't recognized.
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin#using-plugins-block
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#applying-the-plugin
	id("com.google.cloud.tools.appengine-appyaml") version "2.4.5"
}

noArg {
	annotation("com.ghrcosta.planningpoker.util.NoArgConstructor")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

extra["springCloudGcpVersion"] = "4.5.0"
extra["springCloudVersion"] = "2022.0.3"

dependencies {
	// Application domain
	implementation(project(":domain"))

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

	// Base starter + dependencies for Spring Boot & Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Spring Cloud GCP starter + extras
	implementation("com.google.cloud:spring-cloud-gcp-starter")
	implementation("com.google.cloud:spring-cloud-gcp-starter-data-firestore")
	implementation("com.google.cloud:spring-cloud-gcp-starter-logging")

	// Spring Doc - Parses application endpoints and generates Swagger documentation (access /swagger-ui/index.html)
	// https://springdoc.org/v2
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// Base framework for Kotlin tests
	testImplementation(kotlin("test"))

	// This dependency is here due to an upcoming behavior change in Gradle 9.0. See details on:
	// https://docs.gradle.org/8.2/userguide/upgrading_version_8.html#test_framework_implementation_dependencies.
	// Note that Spring may release a new version of Spring Cloud that does this automatically. Whenever a new version
	// of Spring Cloud is available, remove the dependency below and execute the gradle task "build --warning-mode all"
	// to check if the warning disappeared.
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// "./gradlew dependencies" is useful to see the project's dependency tree, to know what adds which library. However,
// it doesn't work as expected here. If executed in the "springboot-flutter" root project it doesn't show anything since
// the root doesn't have dependencies, and if executed in the ":springboot" subproject it fails since it can't find one
// of the plugins (which depends on the setting.gradle from the root project).
// The item below is a workaround: by executing "./gradlew allDependencies" in the root project, it'll automatically
// execute the "dependencies" task on each subproject and show all results together.
allprojects {
	tasks.register<DependencyReportTask>("allDependencies")
}

tasks.register<Exec>("buildFlutter") {
	// https://docs.flutter.dev/deployment/web#building-the-app-for-release
	workingDir = File("${rootDir}/ui/")
	if (Os.isFamily(Os.FAMILY_WINDOWS)) {
		commandLine("cmd", "/c", "flutter", "build", "web")
	} else {
		commandLine("flutter", "build", "web")  // Untested!
	}
}

tasks.register<Delete>("deleteCurrentFlutterBuild") {
	dependsOn("buildFlutter")
	delete("${projectDir}/src/main/resources/static")
}

tasks.register<Copy>("copyFlutterBuild") {
	dependsOn("deleteCurrentFlutterBuild")
	from("${rootDir}/ui/build/web")
	into("${projectDir}/src/main/resources/static")
}

tasks {
	// The task "processResources" is used to handle files in "/resources/static", which is where the compiled Flutter
	// files must be put. In order to ensure that (1) Flutter files are added to the directory before the application
	// JAR is created and (2) that Gradle doesn't complain about two unrelated tasks managing the same directory --
	// "processResources" and "copyFlutterBuild" -- the former must depend on the latter. As a side effect, since this
	// task runs early in the process, the Flutter frontend will be built during tests as well.
	processResources {
		dependsOn("copyFlutterBuild")
	}
}

val appVersion: String by project  // See gradle.properties
val versionAppEngine = "springboot-${appVersion.replace(".","-")}"  // AppEngine allows letters, numbers and hyphen
appengine {
	stage {  // https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#stage-1
		setAppEngineDirectory("./")
		// Artifact = the file to be deployed (see documentation on the link above). Note that besides the JAR below
		// (which was generated by the "bootJar" gradle command), the App Engine Gradle plugin might upload other files
		// as well -- check directory "springboot\build\staged-app".
		setArtifact("${buildDir}/libs/springboot.jar")
	}
	deploy {  // https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#deploy-1
		projectId = "GCLOUD_CONFIG"
		version = versionAppEngine
	}
	tools {  // https://github.com/GoogleCloudPlatform/app-gradle-plugin/blob/master/USER_GUIDE.md#tools-1
		// Note: the "GOOGLE_CLOUD_SDK_HOME" environment variable is not create automatically!
		val googleCloudSdkHome = System.getenv("GOOGLE_CLOUD_SDK_HOME")
		//project.logger.lifecycle("env=${googleCloudSdkHome}")  // How to log things in Gradle
		if (googleCloudSdkHome != null) {
			setCloudSdkHome(googleCloudSdkHome)
		}
	}
}