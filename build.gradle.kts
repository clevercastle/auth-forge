plugins {
    id("java")
    id("jacoco")
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
    id("signing")
}

group = "org.clevercastle"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")

    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("javax.persistence:javax.persistence-api:2.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("com.auth0:java-jwt:4.5.0")

    implementation("com.nimbusds:oauth2-oidc-sdk:11.23.1")

    implementation("software.amazon.awssdk:dynamodb:2.31.31")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.31.31")

    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa:2.7.18")

    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("ch.qos.logback:logback-classic:1.5.18")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        xml.outputLocation = layout.buildDirectory.file("jacoco/jacocoTestReport.xml")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(11)
}

mavenPublishing {
    pom {
        name.set("auth-forge")
        description.set("A description of what my library does.")
        inceptionYear.set("2025")
        url.set("https://github.com/clevercastle/auth-forge/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("ivyxjc")
                name.set("ivyxjc")
                url.set("https://github.com/ivyxjc/")
            }
        }
        scm {
            url.set("https://github.com/clevercastle/auth-forge/")
            connection.set("scm:git:git://github.com/clevercastle/auth-forge.git")
            developerConnection.set("scm:git:ssh://git@github.com/clevercastle/auth-forge.git")
        }
    }
}