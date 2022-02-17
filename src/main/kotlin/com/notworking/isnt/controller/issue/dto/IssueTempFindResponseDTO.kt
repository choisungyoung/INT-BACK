package com.notworking.isnt.controller.issue.dto


data class IssueTempFindResponseDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
) {
}