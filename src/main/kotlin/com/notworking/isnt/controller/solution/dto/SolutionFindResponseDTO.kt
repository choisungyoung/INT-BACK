package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO

data class SolutionFindResponseDTO(
    var id: Long,
    var content: String,
    var docType: String,
    var recommendationCount: Long,
    var developer: DeveloperFindResponseDTO,
    var modifiedDate: String
)