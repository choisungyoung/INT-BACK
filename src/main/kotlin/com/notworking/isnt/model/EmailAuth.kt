package com.notworking.isnt.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "INT_EMAIL_AUTH")
data class EmailAuth(
    @Id
    @GeneratedValue
    var id: Long?,

    @Column(unique = true)
    var email: String,
    var authNum: Int,
    var isAuth: Boolean,
    var expiredDate: LocalDateTime = LocalDateTime.now()

) : BaseTimeEntity() {

    fun update(emailAuth: EmailAuth) {
        this.authNum = emailAuth.authNum
        this.expiredDate = emailAuth.expiredDate
        this.isAuth = emailAuth.isAuth
    }

    fun doAuth() {
        isAuth = true
    }

}


