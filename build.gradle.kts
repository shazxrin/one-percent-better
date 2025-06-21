plugins {
    java
    id("org.springframework.boot") version "3.5.3"
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

openApi {
    customBootRun {
        systemProperties = mapOf("spring.docker.compose.file" to "${projectDir}/compose.yaml")
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.github.com/shazxrin/notifier")
        credentials {
            username = project.findProperty("gpr.username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:1.0.0")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.github.shazxrin.notifier:notifier-common:1.1.0")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    testImplementation("org.springframework.amqp:spring-rabbit-test")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.9")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
