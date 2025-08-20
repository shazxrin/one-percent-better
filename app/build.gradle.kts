plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

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
        systemProperties = mapOf("spring.docker.compose.file" to "${projectDir}/../deploy/dev.compose.yaml")
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.github.com/shazxrin/notifier")
        credentials {
            username = project.findProperty("gpr.username") as String? ?: System.getenv("GPR_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GPR_TOKEN")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:1.0.1")
    }
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-otlp")
    implementation("io.micrometer:micrometer-java21")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.17.1-alpha")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-logback-mdc-1.0:2.17.1-alpha")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.1.2")

    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("io.github.shazxrin.notifier:common:1.2.2")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    testImplementation("org.springframework.amqp:spring-rabbit-test")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.postgresql:postgresql")
    testImplementation("org.testcontainers:postgresql")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.10.3")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.10")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.10")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.awaitility:awaitility:4.3.0")

    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
}
