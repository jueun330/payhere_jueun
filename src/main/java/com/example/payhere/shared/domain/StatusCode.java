package com.example.payhere.shared.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(HttpStatus.OK, "0", "응답이 정상 처리 되었습니다."),
    LOGIN_EMAIL_FAIL(HttpStatus.NOT_FOUND, "110", "해당 하는 Email이 없습니다"),
    LOGIN_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "111", "Password가 일치하지 않습니다."),
    LOGIN_WRONG_SIGNATURE_JWT_TOKEN(HttpStatus.BAD_REQUEST, "112", "잘못된 JWT 서명입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "113", "가계부 작성자가 아닙니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"114","중복된 Email이 있습니다."),
    NOT_FOUND(HttpStatus.BAD_REQUEST,"115","존재하지 않는 가계부 id 입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "116", "금액을 입력하여 주세요.");

    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    StatusCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}