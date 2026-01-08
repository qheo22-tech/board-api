# Seowolseong Board (Spring Boot + S3 File)

기본적으로 로그인 없이 사용할 수 있는 간단 게시판 API입니다.  
게시글은 비밀번호 기반으로 수정/삭제 권한을 검증하며,  
첨부파일은 S3에 저장하고 DB에는 메타데이터를 관리합니다.

추가로 **세션 기반 로그인(Role)** 을 적용해,
관리자 기능에 대한 접근을 제어합니다.

---

## Features
- Post CRUD (soft delete)
- Post password verification (수정/삭제 전 검증)
- 관리자 세션 로그인 및 권한 분기
  - 관리자: 비밀번호 없이 게시글 삭제/복구 가능
  - 일반 사용자: 게시글 조회 및 비밀번호 기반 삭제만 가능
  - 삭제된 게시글은 일반 사용자 목록/상세에서 숨김 처리
- File upload / download / delete (S3 + DB metadata)
- File status management  
  (`PENDING` → `READY` / `FAILED` / `DELETED`)
- Health check endpoint 제공

---

## Tech Stack
- Java / Spring Boot
- Spring Data JPA
- HTTP Session 기반 인증
- AWS SDK v2 (S3Client)
- Database: **PostgreSQL (JPA)**

---

## API Overview

### Auth
- `POST /api/auth/login`  
  로그인 (세션 생성, 사용자 ID 및 Role 저장)
- `POST /api/auth/logout`  
  로그아웃 (세션 무효화)
- `GET /api/auth/me`  
  로그인 상태 확인 (세션 기반)

---

### Posts
- `GET /api/posts`  
  게시글 목록 조회 (최대 50건)  
  - 관리자: 삭제된 게시글 포함 전체 조회  
  - 일반 사용자: 삭제된 게시글 제외
- `GET /api/posts/{id}`  
  게시글 상세 조회 (+ 첨부파일 목록)  
  - 일반 사용자: 삭제된 게시글 접근 불가
- `POST /api/posts`  
  게시글 생성 (JSON)
- `POST /api/posts/{id}/verify-password`  
  게시글 비밀번호 검증
- `POST /api/posts/{id}/update`  
  게시글 수정
- `POST /api/posts/{id}/delete`  
  게시글 삭제 (비밀번호 검증)
- `PATCH /api/posts/{id}/deleted`  
  게시글 삭제/복구 (관리자)
- `GET /api/posts/ping`  
  헬스체크 / 연결 확인용 엔드포인트

---

### Files
- `POST /api/files/upload`  
  파일 업로드 (multipart/form-data)
- `GET /api/files/{fileId}/download`  
  파일 다운로드 (streaming)
- `POST /api/files/{fileId}/delete`  
  파일 삭제 (soft delete + S3 delete)

---

## Authentication / Session

- 로그인 시 HTTP Session이 생성됩니다.
- 세션에는 사용자 식별자와 Role 정보가 저장됩니다.
- 로그아웃 시 세션을 invalidate 처리합니다.
- 게시글 API는 세션 Role에 따라 동작이 분기됩니다.
  - 관리자: 삭제/복구 가능
  - 일반 사용자: 삭제된 게시글 접근 불가
- 프론트엔드는 로그인 상태 확인 API를 통해 관리자 UI를 제어합니다.

---

## File Handling Policy
- 파일은 S3에 저장되며, DB에는 메타데이터만 관리합니다.
- 업로드 시 파일 상태를 `PENDING`으로 생성한 뒤:
  - 업로드 성공 시 `READY`
  - 업로드 실패 시 `FAILED`
- 삭제 시 실제 레코드는 유지하며 `DELETED` 상태로 처리합니다.
- 다운로드는 `READY` 상태의 파일만 허용합니다.

---

## Run

### Required Configuration
`application.yml` 예시 (실제 값은 포함하지 않습니다)

```yml
app:
  s3:
    bucket: <YOUR_S3_BUCKET_NAME>

spring:
  datasource:
    url: jdbc:postgresql://<HOST>:5432/<DB_NAME>
    username: <DB_USER>
    password: <DB_PASSWORD>
