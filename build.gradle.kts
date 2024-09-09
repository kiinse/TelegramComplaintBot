plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm")
}

group = "kiinse.dev.telegram"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.tomlj:tomlj:1.1.0")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.slf4j:slf4j-log4j12:2.0.5")
    implementation("org.mongodb:mongo-java-driver:3.12.12")

    implementation("org.telegram:telegrambots-longpolling:7.7.0")
    implementation("org.telegram:telegrambots-client:7.7.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.jar {
    manifest.attributes["Main-Class"] = "kiinse.dev.telegram.Main"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}