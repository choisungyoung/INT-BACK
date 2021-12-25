package com.notworking.isnt.controller.developer.dto

import com.notworking.isnt.model.Developer

data class DeveloperSaveRequestDTO(
    var email: String,
    var password: String,
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