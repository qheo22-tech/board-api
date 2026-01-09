# 1) build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# (권장) 의존성 캐시를 위해 먼저 복사
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
RUN chmod +x mvnw && ./mvnw -DskipTests dependency:go-offline

# 소스 복사 후 패키징
COPY src src
RUN ./mvnw -DskipTests package

# 2) run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
