package com.notworking.isnt.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "INT_CODE")
data class Code(
    @Id
    @GeneratedValue
    var id: Long?,
    var code: String,
    var name: String,
    var key: String,
    var value: String,
    var description: String,

    ) : BaseTimeEntity() {

}


