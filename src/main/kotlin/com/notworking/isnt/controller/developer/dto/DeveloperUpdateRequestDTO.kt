package com.notworking.isnt.controller.developer.dto

import com.notworking.isnt.model.Developer

data class DeveloperUpdateRequestDTO(
    var email: String,
    var password: String,
    var name: String?,
    var introduction: String,
    var pictureUrl: String?,
    var point: Int? = 0,
    var popularity: Int? = 0
) {
    fun toModel(): Developer = Developer(
        id = null,
        email = this.email,
        pwd = this.password,
        name = this.name,
        introduction = this.introduction,
        pictureUrl = this.pictureUrl,
        point = this.point,
        popularity = this.popularity
    )
}