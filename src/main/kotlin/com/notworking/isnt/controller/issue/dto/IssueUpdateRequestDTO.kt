package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class IssueUpdateRequestDTO(
    @field:NotNull
    var id: Long,
    @field:NotEmpty
    @field:Size(max = 10485760)
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