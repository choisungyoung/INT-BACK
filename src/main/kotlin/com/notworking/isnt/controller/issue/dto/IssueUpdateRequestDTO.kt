package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty

data class IssueUpdateRequestDTO(
    @field:NotEmpty
    var id: Long,
    @field:NotEmpty
    var title: String,
    @field:NotEmpty
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