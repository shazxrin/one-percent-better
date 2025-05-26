# Builder container
FROM bellsoft/liberica-runtime-container:jdk AS builder
WORKDIR /builder

# This points to the built jar file in the build folder
ARG JAR_FILE=build/libs/*.jar

# Copy the jar file to the working directory and rename it to application.jar
COPY ${JAR_FILE} application.jar

# Extract the jar file
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# Runtime container
FROM bellsoft/liberica-runtime-container:jre-cds
WORKDIR /application

# Copy the extracted jar contents from the builder container into the working directory in the runtime container
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Execute the CDS training run
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -Dspring.profiles.active=build -jar application.jar

ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "-jar", "application.jar"]
