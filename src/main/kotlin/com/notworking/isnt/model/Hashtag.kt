package com.notworking.isnt.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "INT_HASHTAG")
data class Hashtag(
    @Id
    @GeneratedValue
    var id: Long?,
    var name: String,

    ) : BaseTimeEntity() {
    fun update(issue: Hashtag): Hashtag? {
        this.name = issue.name
        return this
    }
}


