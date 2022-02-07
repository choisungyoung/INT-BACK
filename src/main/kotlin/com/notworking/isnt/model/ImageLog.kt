package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_IMAGE_LOG")
data class ImageLog(
    @Id
    @GeneratedValue
    var id: Long?,
    // 유저아이디
    var userId: String?,
    // 클래스
    var className: String?,
    // 메소드
    var methodName: String?,
    // request ip
    var requestIp: String?,
    // request url
    var requestUrl: String?,
    // request method
    var requestMethod: String?,
    // request body
    var requestBody: String?,
    // request header
    var requestHeader: String?,
    // request param
    var requestParam: String?,
) : BaseTimeEntity() {
    // response status
    var responseStatus: String? = null

    // response body
    @Column(length = 1024)
    var responseBody: String? = null

    // response header
    var responseHeader: String? = null

    // error stack
    @Column(length = 1024)
    var errorStack: String? = null

    // execution Time
    var executionTime: Long? = null
}


