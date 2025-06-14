# Builder container
FROM bellsoft/liberica-runtime-container:jdk-all AS builder
WORKDIR /builder

# Copy the jar file to the working directory and rename it to application.jar
COPY build/libs/one-percent-better-*.jar application.jar

# Extract the jar file as layers
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# Extract the fat JAR, find dependencies, and create a custom JRE.
RUN jar xf application.jar

RUN jdeps \
    --print-module-deps \
    --ignore-missing-deps \
    -q \
    --recursive \
    --multi-release 24 \
    --class-path 'BOOT-INF/lib/*' \
    application.jar > jre-deps.txt

RUN jlink \
    --add-modules $(cat jre-deps.txt) \
    --output /custom-jre \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress=2

# Runtime container
FROM alpine
WORKDIR /application

# Copy the custom JRE from the builder stage.
ENV PATH="/opt/jre/bin:${PATH}"
COPY --from=builder /custom-jre /opt/jre

# Copy the extracted jar contents from the builder container into the working directory in the runtime container
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Execute the AOT training run and create the AOT cache
# Enable for faster startup, at the expense of bigger image size
# Add "-XX:AOTCache=app.aot" to entry point
#RUN java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -Dspring.context.exit=onRefresh -Dspring.profiles.active=build -jar application.jar
#RUN java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -jar application.jar && rm app.aotconf

ENTRYPOINT [ \
    "java", \
    "-XX:MaxRAMPercentage=0.8", \
    "-jar", "application.jar" \
]
