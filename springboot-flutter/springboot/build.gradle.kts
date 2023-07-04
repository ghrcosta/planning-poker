import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.8.22"

	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion

	// Adds empty constructors to Kotlin data classes, required to insert/retrieve data to/from database.
	// https://kotlinlang.org/docs/no-arg-plugin.html
	id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion  // See section "noArg" below

	// Plugin for AppEngine deployment. The "-appyaml" part is not required but is recommended.
	// https://github.com/GoogleCloudPlatform/app-gradle-plugin
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

// See gradle.properties
val appVersion: String by project
val versionAppEngine = appVersion.replace(".","-")  // AppEngine only allows letters, numbers and hyphen
appengine {
	stage {
		setAppEngineDirectory("./")
		setArtifact("build/libs/springboot.jar")  // JAR generated by "bootJar" gradle command
	}
	deploy {
		projectId = "GCLOUD_CONFIG"
		version = versionAppEngine
	}
}