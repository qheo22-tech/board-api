# Seowolseong Board (Spring Boot + S3 File)

로그인 없이 사용할 수 있는 간단 게시판 API입니다.  
게시글은 비밀번호 기반으로 수정/삭제 권한을 검증하며,  
첨부파일은 S3에 저장하고 DB에는 메타데이터를 관리합니다.

---

## Features
- Post CRUD (soft delete)
- Post password verification (수정/삭제 전 검증)
- File upload / download / delete (S3 + DB metadata)
- File status management  
  (`PENDING` → `READY` / `FAILED` / `DELETED`)
- Health check endpoint 제공

---

## Tech Stack
- Java / Spring Boot
- Spring Data JPA
- AWS SDK v2 (S3Client)
- Database: **PostgreSQL (JPA)**

---

## API Overview

### Posts
- `GET /api/posts`  
  게시글 목록 조회 (최대 50건)
- `GET /api/posts/{id}`  
  게시글 상세 조회 (+ 첨부파일 목록)
- `POST /api/posts`  
  게시글 생성 (JSON)
- `POST /api/posts/{id}/verify-password`  
  게시글 비밀번호 검증
- `POST /api/posts/{id}/update`  
  게시글 수정
- `POST /api/posts/{id}/delete`  
  게시글 삭제 (soft delete)
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
