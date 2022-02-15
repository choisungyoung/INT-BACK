package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.controller.developer.dto.DeveloperFindIssueResponseDTO

data class IssueDetailFindResponseDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
    var hits: Long,
    var recommendationCount: Long,
    var hashtags: List<String>,

    var developer: DeveloperFindIssueResponseDTO,
    var solutions: List<SolutionFindResponseDTO>,
    var modifiedDate: String
)