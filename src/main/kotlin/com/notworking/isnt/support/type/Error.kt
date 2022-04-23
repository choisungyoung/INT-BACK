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
    AUTH_INPUT_INVALID(HttpStatus.BAD_REQUEST.value(), "ATH001", "로그인 입력값이 유효하지 않습니다."),
    AUTH_FAILED(HttpStatus.UNAUTHORIZED.value(), "ATH002", "이메일 혹은 패스워드가 틀렸습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "ATH003", "인증 토큰이 유효하지 않습니다."),
    AUTH_NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED.value(), "ATH004", "인증 토큰이 존재하지 않습니다."),
    AUTH_GIT_HUB(HttpStatus.UNAUTHORIZED.value(), "ATH005", "GitHub인증 사용자입니다."),
    AUTH_INVALID_AUTH_NUM(HttpStatus.UNAUTHORIZED.value(), "ATH006", "인증 번호가 유효하지 않습니다."),
    AUTH_NOT_FOUND_AUTH_NUM(HttpStatus.UNAUTHORIZED.value(), "ATH007", "발급된 인증번호가 없습니다."),
    AUTH_NOT_AUTH_AUTH_NUM(HttpStatus.UNAUTHORIZED.value(), "ATH008", "인증되지 않은 인증정보입니다."),

    // Developer
    DEVELOPER_INPUT_INVALID(HttpStatus.BAD_REQUEST.value(), "DEV001", "사용자 정보가 유효하지 않습니다."),
    DEVELOPER_EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "DEV002", "이미 존재하는 이메일입니다."),
    DEVELOPER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "DEV003", "존재하지 않는 사용자입니다."),
    WITHDRAWAL_DEVELOPER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "DEV004", "탈퇴회원 표시를 위한 계정이 없습니다."),
    DEVELOPER_USERID_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "DEV005", "이미 존재하는 아이디입니다."),
    DEVELOPER_NAME_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "DEV006", "이미 존재하는 닉네임입니다."),
    DEVELOPER_SELF_FOLLOW(HttpStatus.BAD_REQUEST.value(), "DEV007", "자신을 팔로우할 수 없습니다."),
    DEVELOPER_USER_ID_HAS_NOT_HEADER(HttpStatus.BAD_REQUEST.value(), "DEV008", "헤더에 유저아이디가 필요합니다."),
    DEVELOPER_HAS_NOT_AUTH_NUM(HttpStatus.BAD_REQUEST.value(), "DEV009", "인증번호가 발송되지 않은 사용자입니다."),
    DEVELOPER_MODIFY_SELF_ONLY(HttpStatus.BAD_REQUEST.value(), "DEV010", "사용자 본인만 수정가능합니다."),

    // Issue
    ISSUE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "ISU001", "존재하지 않는 이슈입니다."),
    ISSUE_NOT_DEVELOPER(HttpStatus.BAD_REQUEST.value(), "SLT002", "이슈 작성자가 아닙니다."),

    // Solution
    SOLUTION_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "SLT001", "존재하지 않는 솔루션입니다."),
    SOLUTION_NOT_DEVELOPER(HttpStatus.BAD_REQUEST.value(), "SLT002", "솔루션 작성자가 아닙니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "CMT001", "존재하지 않는 이슈입니다."),

    // Code
    CODE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "COD001", "존재하지 않는 코드입니다."),


}