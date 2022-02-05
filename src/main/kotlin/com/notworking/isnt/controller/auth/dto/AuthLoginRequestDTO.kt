package com.notworking.isnt.controller.issue.dto

import javax.validation.constraints.NotNull

data class AuthLoginRequestDTO(
    @field:NotNull
    var username: String,
    @field:NotNull
    var password: String,
)