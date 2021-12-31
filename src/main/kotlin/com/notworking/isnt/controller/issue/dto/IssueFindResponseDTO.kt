package com.notworking.isnt.controller.issue.dto

data class IssueFindResponseDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
    var hits: Long,
    var recommendationCount: Long,
)