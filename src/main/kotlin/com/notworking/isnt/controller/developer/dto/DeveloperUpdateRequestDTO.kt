package com.notworking.isnt.controller.developer.dto

import com.notworking.isnt.model.Developer
import javax.validation.constraints.NotEmpty

data class DeveloperUpdateRequestDTO(
    @field:NotEmpty
    var email: String,
    @field:NotEmpty
    var name: String,
    var introduction: String?,
    var gitUrl: String?,
    var webSiteUrl: String?,
    var groupName: String?,
    var pictureUrl: String?,
    var point: Int? = 0,
    var popularity: Int? = 0
) {
    fun toModel(): Developer = Developer(
        id = null,
        email = this.email,
        pwd = "",
        name = this.name,
        introduction = this.introduction,
        gitUrl = this.gitUrl,
        webSiteUrl = this.webSiteUrl,
        groupName = this.groupName,
        pictureUrl = this.pictureUrl,
        point = this.point,
        popularity = this.popularity
    )
}