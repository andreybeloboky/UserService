FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/user-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]