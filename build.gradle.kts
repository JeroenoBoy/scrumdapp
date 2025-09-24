val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_kotlinxdatetime_version: String by project

plugins {
    kotlin("jvm") version "2.2.20"
    id("io.ktor.plugin") version "3.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

group = "com.jeroenvdg"
version = "0.0.1"

application {
    mainClass = "com.jeroenvdg.scrumdapp.ApplicationKt"
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-mustache")
    implementation("io.ktor:ktor-server-html-builder:$exposed_version")
    implementation("io.ktor:ktor-server-di")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_kotlinxdatetime_version")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.h2database:h2:$h2_version")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-client-cio:$exposed_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-serialization-jackson:3.3.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("io.ktor:ktor-client-encoding:3.3.0")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
