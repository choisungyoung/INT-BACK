package com.notworking.isnt.support.type

enum class Error(
    val status: Int,
    val code: String,
    val message: String
) {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", "Invalid Input Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),

    // Auth(login)
    LOGIN_INPUT_INVALID(400, "M002", "로그인 입력값이 유효하지 않습니다."),
    LOGIN_FAILED(400, "M002", "이메일 혹은 패스워드가 틀렸습니다."),

    // Developer
    DEVELOPER_INPUT_INVALID(400, "M001", "사용자 정보가 유효하지 않습니다."),
    DEVELOPER_EMAIL_DUPLICATION(400, "M001", "이미 존재하는 이메일입니다."),
    DEVELOPER_NOT_FOUND(400, "M002", "존재하지 않는 사용자입니다."),

    // Issue
    ISSUE_NOT_FOUND(400, "M002", "존재하지 않는 이슈입니다."),

}