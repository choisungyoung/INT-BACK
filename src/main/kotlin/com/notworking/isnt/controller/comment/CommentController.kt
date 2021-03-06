package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.CommentFindResponseDTO
import com.notworking.isnt.controller.issue.dto.CommentSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.CommentUpdateRequestDTO
import com.notworking.isnt.model.Comment
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/comment")
@RestController
class CommentController(var commentService: CommentService) {
    /** 댓글목록 오래된 순 */
    @GetMapping("/list/{solutionId}")
    fun findList(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createdDate"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable,
        @PathVariable solutionId: Long
    ): ResponseEntity<Page<CommentFindResponseDTO>> {
        var page: Page<Comment> = commentService.findAllComment(pageable, solutionId)
        var list: List<CommentFindResponseDTO> = page.stream()
            .map { entity ->
                CommentFindResponseDTO(
                    entity.id!!,
                    entity.content,
                    entity.getModifiedDate(),
                    DeveloperFindResponseDTO(
                        email = entity.developer.email,
                        name = entity.developer.name,
                        introduction = entity.developer.introduction,
                        gitUrl = entity.developer.gitUrl,
                        webSiteUrl = entity.developer.webSiteUrl,
                        groupName = entity.developer.groupName,
                        pictureUrl = entity.developer.pictureUrl,
                        point = entity.developer.point,
                        popularity = entity.developer.popularity
                    )
                )
            }.toList()

        return ResponseEntity.ok().body(PageImpl<CommentFindResponseDTO>(list, pageable, page.totalElements))
    }

    /** 이슈 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: CommentSaveRequestDTO): ResponseEntity<Void> {

        var user = SecurityContextHolder.getContext().authentication.principal as User?
        user ?: throw BusinessException(Error.DEVELOPER_NOT_FOUND)

        commentService.saveComment(dto.toModel(), user.username, dto.solutionId)

        return ResponseEntity.ok().build()
    }

    /** 이슈 수정 */
    @PutMapping
    fun update(@RequestBody dto: CommentUpdateRequestDTO): ResponseEntity<Void> {
        commentService.updateComment(dto.toModel())

        return ResponseEntity.ok().build()
    }


    /** 이슈 삭제 */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        commentService.deleteComment(id)

        return ResponseEntity.ok().build()
    }
}