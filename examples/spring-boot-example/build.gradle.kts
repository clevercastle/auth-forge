import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.clevercastle"
version = "0.1.0-SNAPSHOT"
dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        //Testing dependencies
        mavenBom("org.junit:junit-bom:5.13.4")
        mavenBom("software.amazon.awssdk:bom:2.34.4")
        mavenBom("org.testcontainers:testcontainers-bom:1.21.3")
        mavenBom("com.fasterxml.jackson:jackson-bom:2.19.2")
    }
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation(project(":core"))
    implementation(project(":impls:impl-postgres"))

    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.4")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.6")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("org.postgresql:postgresql:42.7.7")

    implementation("com.auth0:java-jwt:4.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
