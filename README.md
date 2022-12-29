## 페이히어 백엔드 엔지니어 과제 



### API 설계

#### 1) 회원가입 : `POST` /ph/signup

#### request body

```
{
    "email" : String,
    "pw" : String,
    "pwConfirm" : String
}
```

#### 2) 로그인 : `POST` /ph/login

#### request body

```
{
    "email" : String,
    "pw" : String
}
```

#### 3) 로그아웃 : `POST` /ph/logout

#### 4) 가계부 생성 : `POST` /ph/accounts

#### request body

```
{
    "money" : Integer,
    "memo" : String
}
```
#### 5) 가계부 수정 : 'PUT' /ph/accounts/{account_id}

#### request body

```
{
    "money" : Integer,
    "memo" : String
}
```

#### 6) 가계부 삭제 : `DELETE` /ph/accounts/{account_id}

#### 7) 가계부 복원 : `GET` /ph/accounts/restore/{account_id}

#### 8) 가계부 목록 : `GET` /ph/accounts

#### 9) 가계부 세부 목록 : `GET` /ph/accounts/details



