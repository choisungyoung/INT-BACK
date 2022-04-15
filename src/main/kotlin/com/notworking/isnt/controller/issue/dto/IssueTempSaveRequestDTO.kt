package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.IssueTemp
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class IssueTempSaveRequestDTO(
    @field:NotEmpty
    var title: String,
    @field:NotEmpty
    @field:Size(max = 10485760)
    var content: String,
    @field:NotEmpty
    var docType: String,
    var category: String?,
) {
    fun toModel(): IssueTemp = IssueTemp(
        id = null,
        title = this.title,
        content = this.content,
        docType = DocType.valueOf(docType),
        category = this.category
    )
}