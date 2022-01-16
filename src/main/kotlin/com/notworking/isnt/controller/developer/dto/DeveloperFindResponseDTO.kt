package com.notworking.isnt.controller.developer.dto

data class DeveloperFindResponseDTO(
    var userId: String,
    var email: String,
    var name: String?,
    var introduction: String?,
    var gitUrl: String?,
    var webSiteUrl: String?,
    var pictureUrl: String?,
    var point: Int? = 0,
    var popularity: Int? = 0
)