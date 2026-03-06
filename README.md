# Spring Advanced

본 프로젝트는 Spring Boot와 JPA를 사용하여 구현한 일정 관리 서버이다.
3 Layer Architecture를 기반으로 설계되었으며, JWT 기반 인증/인가를 적용하였다.

---

## 테스트 커버리지

<img width="1061" height="632" alt="Image" src="https://github.com/user-attachments/assets/e641fa72-a524-4a36-a09e-e31342b76992" />

---

## 개발 환경

- **Language**: Java 17
- **Framework**: Spring Boot 3.3.3
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **IDE**: IntelliJ

---


**Base URL**: http://localhost:8080

| 기능 | Method | URL |
|------|--------|-----|
| 회원가입 | POST | `/auth/signup` |
| 로그인 | POST | `/auth/signin` |
| 유저 조회 | GET | `/users/{userId}` |
| 비밀번호 변경 | PUT | `/users` |
| 유저 권한 변경 | PATCH | `/admin/users/{userId}` |
| 댓글 삭제 | DELETE | `/admin/comments/{commentId}` |
| 일정 생성 | POST | `/todos` |
| 일정 목록 조회 | GET | `/todos` |
| 일정 단건 조회 | GET | `/todos/{todoId}` |
| 댓글 작성 | POST | `/todos/{todoId}/comments` |
| 댓글 목록 조회 | GET | `/todos/{todoId}/comments` |
| 담당자 등록 | POST | `/todos/{todoId}/managers` |
| 담당자 목록 조회 | GET | `/todos/{todoId}/managers` |
| 담당자 삭제 | DELETE | `/todos/{todoId}/managers/{managerId}` |

### 1. 인증

#### 1-1. 회원가입
- Method: `POST`
- URL: `/auth/signup`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "email": "이메일",
  "password": "비밀번호",
  "userRole": "USER"
}
```
#### Response Body
```json
{
  "bearerToken": "JWT 토큰"
}
```

#### 1-2. 로그인
- Method: `POST`
- URL: `/auth/signin`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "email": "이메일",
  "password": "비밀번호"
}
```
#### Response Body
```json
{
  "bearerToken": "JWT 토큰"
}
```
<br>

### 2. 유저

#### 2-1. 유저 조회
- Method: `GET`
- URL: `/users/{userId}`
- Status Code: `200 OK`

#### Response Body
```json
{
  "id": 1,
  "email": "이메일"
}
```

#### 2-2. 비밀번호 변경
- Method: `PUT`
- URL: `/users`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "oldPassword": "기존 비밀번호",
  "newPassword": "새 비밀번호"
}
```
<br>

### 3. 관리자

#### 3-1. 유저 권한 변경
- Method: `PATCH`
- URL: `/admin/users/{userId}`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "role": "ADMIN"
}
```

#### 3-2. 댓글 삭제
- Method: `DELETE`
- URL: `/admin/comments/{commentId}`
- Status Code: `200 OK`
<br>

### 4. 일정

#### 4-1. 일정 생성
- Method: `POST`
- URL: `/todos`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "title": "제목",
  "contents": "내용"
}
```
#### Response Body
```json
{
  "id": 1,
  "title": "제목",
  "contents": "내용",
  "weather": "날씨",
  "user": {
    "id": 1,
    "email": "이메일"
  }
}
```

#### 4-2. 일정 목록 조회
- Method: `GET`
- URL: `/todos`
- Query Parameters: `page` (기본값 1), `size` (기본값 10)
- Status Code: `200 OK`

#### Response Body
```json
{
  "content": [
    {
      "id": 1,
      "title": "제목",
      "contents": "내용",
      "weather": "날씨",
      "user": {
        "id": 1,
        "email": "이메일"
      },
      "createdAt": "0000-00-00T00:00:00",
      "modifiedAt": "0000-00-00T00:00:00"
    }
  ]
}
```

#### 4-3. 일정 단건 조회
- Method: `GET`
- URL: `/todos/{todoId}`
- Status Code: `200 OK`

#### Response Body
```json
{
  "id": 1,
  "title": "제목",
  "contents": "내용",
  "weather": "날씨",
  "user": {
    "id": 1,
    "email": "이메일"
  },
  "createdAt": "0000-00-00T00:00:00",
  "modifiedAt": "0000-00-00T00:00:00"
}
```
<br>

### 5. 댓글

#### 5-1. 댓글 작성
- Method: `POST`
- URL: `/todos/{todoId}/comments`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "contents": "댓글 내용"
}
```
#### Response Body
```json
{
  "id": 1,
  "contents": "댓글 내용",
  "user": {
    "id": 1,
    "email": "이메일"
  }
}
```

#### 5-2. 댓글 목록 조회
- Method: `GET`
- URL: `/todos/{todoId}/comments`
- Status Code: `200 OK`

#### Response Body
```json
[
  {
    "id": 1,
    "contents": "댓글 내용",
    "user": {
      "id": 1,
      "email": "이메일"
    }
  }
]
```
<br>

### 6. 담당자

#### 6-1. 담당자 등록
- Method: `POST`
- URL: `/todos/{todoId}/managers`
- Content-Type: `application/json`
- Status Code: `200 OK`

#### Request Body
```json
{
  "managerUserId": 2
}
```
#### Response Body
```json
{
  "id": 1,
  "user": {
    "id": 2,
    "email": "이메일"
  }
}
```

#### 6-2. 담당자 목록 조회
- Method: `GET`
- URL: `/todos/{todoId}/managers`
- Status Code: `200 OK`

#### Response Body
```json
[
  {
    "id": 1,
    "user": {
      "id": 2,
      "email": "이메일"
    }
  }
]
```

#### 6-3. 담당자 삭제
- Method: `DELETE`
- URL: `/todos/{todoId}/managers/{managerId}`
- Status Code: `200 OK`
<br>
