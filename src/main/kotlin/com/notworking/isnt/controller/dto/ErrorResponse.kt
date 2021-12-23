package com.notworking.isnt.controller.dto

data class ErrorResponse(
    val code: String,
    val guid: String = "",
    val title: String = "에러",
    val message: String = "에러가 발생하였습니다.",
)