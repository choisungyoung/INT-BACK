package com.notworking.isnt.controller.developer.dto

data class DeveloperFindResponseDTO(
    var email: String,
    var password: String,
    var name: String?,
    var introduction: String,
    var pictureUrl: String?,
    var point: Int? = 0,
    var popularity: Int? = 0
)