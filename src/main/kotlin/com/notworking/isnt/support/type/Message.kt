package com.notworking.isnt.support.type

enum class Message(
    val message: String
) {
    // Mail
    AUTH_EMAIL_TITLE("인증번호 발송"),
    AUTH_FIND_PASSWORD_EMAIL_MESSAGE("NotWorking 계정의 비밀번호 변경을 위한 인증번호는 [%d]입니다."),
    AUTH_SIGN_UP_EMAIL_MESSAGE("NotWorking 계정생성을 위한 이메일 인증번호는 [%d]입니다."),

}