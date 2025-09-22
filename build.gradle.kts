plugins {
    id("java")
    id("jacoco")
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
    id("signing")
}

group = "org.clevercastle"
version = "0.0.1-SNAPSHOT"


subprojects {
    repositories {
        mavenCentral()
    }
    apply {
        plugin("java")
        plugin("jacoco")
        plugin("com.vanniktech.maven.publish")
        plugin("signing")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(11)
    }
}

