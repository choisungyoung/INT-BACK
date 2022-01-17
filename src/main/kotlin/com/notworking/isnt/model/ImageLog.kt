package com.notworking.isnt.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "INT_DEVELOPER")
data class ImageLog(
    @Id
    @GeneratedValue
    var id: Long?,
    // 유저아이디
    // 클래스
    // 메소드
    // request url
    // request body
    // request header
    // request param
    // response status
    // response body
    // response header
    // error stack
) : BaseTimeEntity() {
}


