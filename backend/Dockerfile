FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
