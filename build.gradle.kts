plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "io.github.shazxrin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

openApi {
    customBootRun {
        systemProperties = mapOf("spring.docker.compose.file" to "${projectDir}/compose.yaml")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Delete>("cleanWebApp") {
    delete(file("${projectDir}/build/resources/main/public"))
    delete(file("${projectDir}/webapp/build"))
}

tasks.register<Copy>("bundleWebApp") {
    from(file("${projectDir}/webapp/build/client"))
    into(file("${projectDir}/build/resources/main/public"))
}

tasks.named("compileJava") {
    dependsOn(tasks.named("bundleWebApp"))
}
