package com.notworking.isnt.controller.developer.dto

import javax.validation.constraints.NotEmpty

data class DeveloperUpdatePasswordRequestDTO(
    @field:NotEmpty
    var userId: String,
    @field:NotEmpty
    var password: String,
) {
}