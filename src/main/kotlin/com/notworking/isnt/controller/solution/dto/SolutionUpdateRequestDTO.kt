package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Solution
import com.notworking.isnt.support.type.DocType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class SolutionUpdateRequestDTO(
    @field:NotEmpty
    var id: Long,
    @field:NotEmpty
    @field:Size(max = 10485760)
    var content: String,
    var docType: String,
) {
    fun toModel(): Solution = Solution(
        id = id,
        content = this.content,
        docType = DocType.valueOf(docType),
    )
}