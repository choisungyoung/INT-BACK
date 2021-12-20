package com.notworking.int.controller.dto

import com.notworking.int.model.Developer
import com.notworking.int.support.type.Role

data class DeveloperDTO(
    var name: String,
    var email: String,
    var password: String,
    var pictureUrl: String?,
) {
    fun toModel(): Developer = Developer(
        id = null,
        email = this.email,
        pwd = this.password,
        name = this.name,
        pictureUrl = this.pictureUrl,
    )
}