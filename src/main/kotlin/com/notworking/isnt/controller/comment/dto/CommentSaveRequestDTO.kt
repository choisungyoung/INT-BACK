package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.model.Comment
import javax.validation.constraints.NotEmpty

data class CommentSaveRequestDTO(
    @field:NotEmpty
    var postId: Long,
    @field:NotEmpty
    var postType: String,
    @field:NotEmpty
    var content: String,
) {
    fun toModel(): Comment = Comment(
        id = null,
        content = this.content,
    )
}