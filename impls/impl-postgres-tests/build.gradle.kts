plugins {
    java
}

// Java 17 for all code in this module
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

dependencies {
    // Depend on production code but avoid bringing conflicting Spring/Jakarta APIs
    testImplementation(project(":impls:impl-postgres"))
    testImplementation(project(":core"))

    // Test frameworks
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Spring Boot 3.x (requires Java 17)
    testRuntimeOnly("org.springframework:spring-context:6.2.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.6")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.6")

    // Testcontainers
    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")

    // PostgreSQL driver
    testRuntimeOnly("org.postgresql:postgresql:42.7.8")
}

tasks.test {
    useJUnitPlatform()
}
