package com.notworking.isnt.controller.issue.dto

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO

data class CommentFindResponseDTO(
    var id: Long,
    var content: String,
    var createdDate: String,
    var modifiedDate: String,
    
    var developer: DeveloperFindResponseDTO
)