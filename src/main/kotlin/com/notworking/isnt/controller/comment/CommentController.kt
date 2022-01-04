package com.notworking.isnt.controller.issue

import com.notworking.isnt.controller.developer.dto.DeveloperFindResponseDTO
import com.notworking.isnt.controller.issue.dto.CommentFindResponseDTO
import com.notworking.isnt.controller.issue.dto.CommentSaveRequestDTO
import com.notworking.isnt.controller.issue.dto.CommentUpdateRequestDTO
import com.notworking.isnt.model.Comment
import com.notworking.isnt.service.CommentService
import com.notworking.isnt.support.exception.BusinessException
import com.notworking.isnt.support.type.Error
import com.notworking.isnt.support.type.PostType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.toList

@RequestMapping("/api/comment")
@RestController
class CommentController(var commentService: CommentService) {

    var email = "commentTester@naver.com" // TODO: Authentication 시큐리티 객체에서 받아오는 것으로 수정

    /** 이슈 조회 */
    @GetMapping("/{id}")
    fun find(@PathVariable id: Long): ResponseEntity<CommentFindResponseDTO> {
        var c: Comment = Comment(null, "")

        var dto: CommentFindResponseDTO? = commentService.findComment(id)?.let {
            CommentFindResponseDTO(
                it.id!!,
                it.content,
                it.getCreatedDate(),
                it.getModifiedDate(),
                DeveloperFindResponseDTO(
                    it.developer.email,
                    it.developer.name,
                    it.developer.introduction,
                    it.developer.pictureUrl,
                    it.developer.point,
                    it.developer.popularity
                )
            )
        }

        //존재하지 않을 경우 에러처리
        dto ?: throw BusinessException(Error.COMMENT_NOT_FOUND)
        return ResponseEntity.ok().body(dto)
    }

    /** 이슈 최신순 목록 조회 */
    @GetMapping("/list/latest")
    fun findList(
        @PageableDefault(
            size = 10,
            page = 0,
            sort = ["createDate"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CommentFindResponseDTO>> {
        var page: Page<Comment> = commentService.findAllComment(pageable)
        var list: List<CommentFindResponseDTO> = page.stream()
            .map { entity ->
                CommentFindResponseDTO(
                    entity.id!!,
                    entity.content,
                    entity.getCreatedDate(),
                    entity.getModifiedDate(),
                    DeveloperFindResponseDTO(
                        entity.developer.email,
                        entity.developer.name,
                        entity.developer.introduction,
                        entity.developer.pictureUrl,
                        entity.developer.point,
                        entity.developer.popularity
                    )
                )
            }.toList()

        return ResponseEntity.ok().body(PageImpl<CommentFindResponseDTO>(list, pageable, page.totalElements))
    }

    /** 이슈 저장 */
    @PostMapping
    fun save(@Valid @RequestBody dto: CommentSaveRequestDTO): ResponseEntity<Void> {
        commentService.saveComment(dto.toModel(), email, dto.postId, PostType.valueOf(dto.postType))

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