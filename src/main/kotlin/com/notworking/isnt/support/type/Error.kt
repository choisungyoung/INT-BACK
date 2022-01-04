package com.notworking.isnt.support.type

import org.springframework.http.HttpStatus

enum class Error(
    val status: Int,
    val code: String,
    val message: String
) {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "CMN001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "CMN002", "Invalid Input Value"),
    HANDLE_ACCESS_DENIED(403, "CMN006", "Access is Denied"),

    // Auth(login)
    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST.value(), "ATH001", "로그인 입력값이 유효하지 않습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED.value(), "ATH002", "이메일 혹은 패스워드가 틀렸습니다."),

    // Developer
    DEVELOPER_INPUT_INVALID(HttpStatus.BAD_REQUEST.value(), "DEV001", "사용자 정보가 유효하지 않습니다."),
    DEVELOPER_EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "DEV002", "이미 존재하는 이메일입니다."),
    DEVELOPER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "DEV003", "존재하지 않는 사용자입니다."),

    // Issue
    ISSUE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "ISU001", "존재하지 않는 이슈입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "CMT001", "존재하지 않는 이슈입니다."),


}