package com.notworking.isnt.controller.developer.dto

import com.notworking.isnt.model.Developer
import javax.validation.constraints.NotNull

data class DeveloperSaveRequestDTO(
    @NotNull
    var email: String,
    @NotNull
    var password: String,
    @NotNull
    var name: String?,
    var introduction: String,
) {
    fun toModel(): Developer = Developer(
        id = null,
        email = this.email,
        pwd = this.password,
        name = this.name,
        introduction = this.introduction,
        pictureUrl = "",    // TODO : 따로 넣기
        point = 0,
        popularity = 0
    )
}