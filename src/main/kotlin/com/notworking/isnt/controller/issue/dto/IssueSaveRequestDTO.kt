package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType

data class IssueSaveRequestDTO(
    var title: String,
    var content: String,
    var docType: String,
) {
    fun toModel(): Issue = Issue(
        id = null,
        title = this.title,
        content = this.content,
        docType = DocType.valueOf(docType),
    )
}