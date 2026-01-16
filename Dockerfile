# 1) Build Stage (컴파일 및 패키징)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# [중요] 빌드 도구와 라이브러리 설정파일만 먼저 복사
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# [중요] Maven Wrapper 실행 권한 부여 및 의존성 미리 다운로드
# 소스 코드가 없어도 라이브러리부터 받아서 '레이어 캐싱'을 활용합니다.
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# [핵심] 이제 진짜 소스 코드를 복사 (여기서부터 사용자가 수정한 코드가 반영됨)
# 아까 발생한 'Main class' 에러를 방지하기 위해 빌드 전에 반드시 복사해야 합니다.
COPY src src

# 실제 실행 파일(.jar) 생성 (테스트는 스킵하여 빌드 속도 향상)
RUN ./mvnw clean package -Dmaven.test.skip=true

# 2) Run Stage (실제 실행 환경)
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일만 쏙 빼와서 가벼운 실행 환경으로 옮깁니다.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 컨테이너 실행 시 자바 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]