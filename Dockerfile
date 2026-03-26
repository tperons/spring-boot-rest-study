# FROM eclipse-temurin:25-jdk

# COPY target/*.jar app.jar

# ENTRYPOINT ["java", "-jar", "/app.jar"]




FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests


FROM builder AS layers
WORKDIR /build
RUN java -Djarmode=layertools -jar target/*.jar extract


FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir uploads && chown spring:spring uploads

USER spring:spring

COPY --from=layers /build/dependencies/ ./
COPY --from=layers /build/spring-boot-loader/ ./
COPY --from=layers /build/snapshot-dependencies/ ./
COPY --from=layers /build/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
