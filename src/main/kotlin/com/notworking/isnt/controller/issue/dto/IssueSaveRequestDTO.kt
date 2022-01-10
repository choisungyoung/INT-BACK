package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Issue
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class IssueSaveRequestDTO(
    @field:NotEmpty
    var title: String,
    @field:NotEmpty
    @field:Size(max = 10485760)
    var content: String,
    @field:NotEmpty
    var docType: String,
) {
    fun toModel(): Issue = Issue(
        id = null,
        title = this.title,
        content = this.content,
        docType = DocType.valueOf(docType),
    )
}