FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
CMD ["./gradlew", "test"]