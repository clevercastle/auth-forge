plugins {
    id("java")
    id("jacoco")
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
    id("signing")
}

group = "org.clevercastle"
version = "0.0.1-SNAPSHOT"

allprojects {
    group = "org.clevercastle"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("jacoco")
        plugin("com.vanniktech.maven.publish")
        plugin("signing")
    }

    dependencies {
        implementation("org.apache.commons:commons-lang3:3.18.0")
        implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(21)
    }
}

