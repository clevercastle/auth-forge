plugins {
    id("java")
    id("jacoco")
}

group = "org.clevercastle"
version = "0.1.0-SNAPSHOT"

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