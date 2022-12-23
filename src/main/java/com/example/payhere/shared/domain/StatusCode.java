package com.example.payhere.shared.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(HttpStatus.OK, "0", "응답이 정상 처리 되었습니다."),
    LOGIN_EMAIL_FAIL(HttpStatus.NOT_FOUND, "110", "해당 하는 Email이 없습니다"),
    LOGIN_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "111", "Password가 틀렸습니다."),
    LOGIN_WRONG_SIGNATURE_JWT_TOKEN(HttpStatus.BAD_REQUEST, "112", "잘못된 JWT 서명입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"117","중복된 Email이 있습니다."),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST,"119","Password가 틀립니다."),
    NOT_FOUND(HttpStatus.BAD_REQUEST,"120","존재하지 않는 가계부 id 입니다."),
    INTERNAL_SERVER_ERROR_PLZ_CHECK(HttpStatus.INTERNAL_SERVER_ERROR, "999", "알수없는 서버 내부 에러 발생 , bangjueun62@gmail.com 으로 연락 부탁드립니다.");

    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    StatusCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}