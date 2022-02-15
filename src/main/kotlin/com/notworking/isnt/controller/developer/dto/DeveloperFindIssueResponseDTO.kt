package com.notworking.isnt.controller.developer.dto

data class DeveloperFindIssueResponseDTO(
    var userId: String,
    var email: String,
    var name: String?,
    var introduction: String?,
    var gitUrl: String?,
    var webSiteUrl: String?,
    var groupName: String?,
    var pictureUrl: String?,
    var point: Int? = 0,
    var popularity: Int? = 0,
    var followYn: Boolean? = false
)