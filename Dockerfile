FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy pre-built JAR
COPY target/databridge-config-service-0.1.0-SNAPSHOT.jar app.jar


EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]

