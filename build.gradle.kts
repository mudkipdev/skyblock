plugins {
    java
    application
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

group = "io.github.unjoinable"
version = "1.0-SNAPSHOT"
application.mainClass = "net.skyblock.Skyblock"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

repositories {
    mavenCentral()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        dependsOn(build)
    }
}

dependencies {
    implementation("net.minestom:minestom-snapshots:1_21_5-aa17002536")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("com.google.code.gson:gson:2.11.0")
}