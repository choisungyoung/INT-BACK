package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Solution
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class SolutionSaveRequestDTO(
    @field:NotEmpty
    var content: String,
    @field:NotEmpty
    @field:Size(max = 10485760)
    var docType: String,
    @field:NotNull
    var issueId: Long,
) {
    fun toModel(): Solution = Solution(
        id = null,
        content = this.content,
        docType = DocType.valueOf(docType),
    )
}