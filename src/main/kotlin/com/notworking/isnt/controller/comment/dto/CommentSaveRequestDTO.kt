package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Comment
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CommentSaveRequestDTO(
    @field:NotNull
    var solutionId: Long,
    @field:NotEmpty
    var content: String,
) {
    fun toModel(): Comment = Comment(
        id = null,
        content = this.content,
    )
}