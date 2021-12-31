package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType

data class IssueUpdateRequestDTO(
    var id: Long,
    var title: String,
    var content: String,
    var docType: String,
) {
    fun toModel(): Issue = Issue(
        id = id,
        title = this.title,
        content = this.content,
        docType = DocType.valueOf(docType),
    )
}