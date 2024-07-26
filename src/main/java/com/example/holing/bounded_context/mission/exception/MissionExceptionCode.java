package com.example.holing.bounded_context.mission.exception;

import com.example.holing.base.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum MissionExceptionCode implements ExceptionCode {
    ALREADY_EXISTS(HttpStatus.CONFLICT, "미션이 최신입니다."),
    ALREADY_UPDATED(HttpStatus.CONFLICT, "이미 미션을 교체하셨습니다."),
    ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 미션입니다."),
    UPDATED_DENIED(HttpStatus.BAD_REQUEST, "날짜가 지난 미션은 교체할 수 없습니다.");

    HttpStatus httpStatus;
    String cause;

    MissionExceptionCode(HttpStatus httpStatus, String cause) {
        this.httpStatus = httpStatus;
        this.cause = cause;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCause() {
        return cause;
    }

    @Override
    public String getName() {
        return name();
    }

}
