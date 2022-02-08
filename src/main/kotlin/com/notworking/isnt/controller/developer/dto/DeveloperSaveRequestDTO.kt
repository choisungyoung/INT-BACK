package com.notworking.isnt.controller.developer.dto

import com.notworking.isnt.model.Developer
import javax.validation.constraints.NotEmpty

data class DeveloperSaveRequestDTO(
    @field:NotEmpty
    var userId: String,
    @field:NotEmpty
    var email: String,
    @field:NotEmpty
    var password: String,
    @field:NotEmpty
    var name: String,
    var introduction: String?,
    var gitUrl: String?,
    var webSiteUrl: String?,
    var groupName: String?,
) {
    fun toModel(): Developer = Developer(
        id = null,
        userId = this.userId,
        email = this.email,
        pwd = this.password,
        name = this.name,
        introduction = this.introduction,
        gitUrl = this.gitUrl,
        webSiteUrl = this.webSiteUrl,
        groupName = this.groupName,
        pictureUrl = "",    // TODO : 따로 넣기
        point = 0,
        popularity = 0
    )
}