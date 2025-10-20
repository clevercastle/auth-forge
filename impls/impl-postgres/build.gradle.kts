java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

dependencies {
    implementation(project(":core"))

    implementation("org.springframework:spring-context:6.2.12")
    compileOnly("org.springframework.data:spring-data-commons:3.5.5") {
        isTransitive = false
    }
    compileOnly("org.springframework.data:spring-data-jpa:3.5.5") {
        isTransitive = false
    }

    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

    // MapStruct dependencies
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}
