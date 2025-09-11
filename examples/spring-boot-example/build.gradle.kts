plugins {
    java
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.clevercastle"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation(rootProject)

    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.4")

    implementation("software.amazon.awssdk:dynamodb:2.31.31")
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.31.31")

    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:42.7.7")
    implementation("com.auth0:auth0:2.24.0")
    implementation("com.auth0:java-jwt:4.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
