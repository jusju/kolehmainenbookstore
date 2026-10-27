# Build stage: compile the Spring Boot jar with Maven + JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Download dependencies first so they are cached between builds
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
# Tests need the MySQL database, so they are skipped in the image build
RUN mvn -q -B package -DskipTests

# Runtime stage: only the JRE and the jar
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app

# application.properties expects the HTTPS keystore at this path
COPY --chown=root:app --chmod=640 springboot-example.p12 /etc/ssl/private/springboot-example.p12
COPY --from=build /app/target/kolehmainenbookstore-0.0.1-SNAPSHOT.jar /app/app.jar

USER app
EXPOSE 8090

# Override the database with environment variables, e.g.
#   SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/iinak
#   SPRING_DATASOURCE_USERNAME=iinak
#   SPRING_DATASOURCE_PASSWORD=...
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
