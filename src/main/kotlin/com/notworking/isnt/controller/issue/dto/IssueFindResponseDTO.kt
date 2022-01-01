package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO

data class IssueFindResponseDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
    var hits: Long,
    var recommendationCount: Long,

    var developer: DeveloperFindResponseDTO
)