package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Comment
import javax.validation.constraints.NotEmpty

data class CommentUpdateRequestDTO(
    @field:NotEmpty
    var id: Long,
    @field:NotEmpty
    var content: String,
) {
    fun toModel(): Comment = Comment(
        id = id,
        content = this.content,
    )
}