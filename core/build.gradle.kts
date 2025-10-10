dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.19.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")

    implementation("javax.persistence:javax.persistence-api:2.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    implementation("javax.transaction:javax.transaction-api:1.3")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

    // todo, just use one of jwt libs
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("com.nimbusds:oauth2-oidc-sdk:11.23.1")

    implementation("commons-codec:commons-codec:1.19.0")
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.4"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.postgresql:postgresql:42.7.4")
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
