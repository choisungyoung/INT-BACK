package com.notworking.isnt.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

/**
 * 자동으로 생성시간, 수정시간 생성을 위한 Entity 클래스
 */
@MappedSuperclass // 필드들을 컬럼으로 인식되도록 함
@EntityListeners(AuditingEntityListener::class) // Auditing기능 포함
open abstract class BaseTimeEntity {
    @CreatedDate // entity가 생성되어 저장될 때 시간이 자동 저장됨
    protected var createDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate // 조회한 entity 값을 변경할 때 시간이 자동 저장됨
    protected var modifiedDate: LocalDateTime = LocalDateTime.now()
}