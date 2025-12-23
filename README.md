# MyProject

Spring Boot 기반 게시판 REST API 프로젝트입니다.  
로컬 / 운영 환경 분리를 고려해 설정을 구성했습니다.

## Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

## Getting Started

### Prerequisites
- JDK 17 이상
- PostgreSQL

### Run (Local)
```bash
./mvnw spring-boot:run

## Configuration
- 환경별 설정은 Spring Profile로 분리 가능하게 구성했습니다. (현재는 local 환경만 사용)
- `application-local.yml`은 gitignore 처리했습니다.