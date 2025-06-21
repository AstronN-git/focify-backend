# build
FROM gradle:jdk21 AS build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.gradle gradle clean build -x test --no-daemon

# run
FROM eclipse-temurin:21 AS run
WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]