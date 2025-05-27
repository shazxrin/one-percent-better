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

# Execute the AOT training run and create the AOT cache
RUN java -Dspring.aot.enabled=true -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -Dspring.context.exit=onRefresh -Dspring.profiles.active=build -jar application.jar
RUN java -Dspring.aot.enabled=true -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar application.jar

ENTRYPOINT ["java", "-Dspring.aot.enabled=true", "-XX:AOTCache=app.aot", "-jar", "application.jar"]
