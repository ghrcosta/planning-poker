plugins {
    kotlin("jvm") version "1.9.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.8")

    // Base framework for Kotlin tests
    testImplementation(kotlin("test"))

    // To allow executing "suspend" functions in tests without having to manually mess with Dispatchers
    // See: https://craigrussell.io/2021/12/testing-android-coroutines-using-runtest/
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
}

tasks.test {
    useJUnitPlatform()
}