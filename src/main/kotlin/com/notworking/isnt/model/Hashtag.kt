package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_HASHTAG")
data class Hashtag(
    @Id
    @GeneratedValue
    var id: Long?,

    var name: String,

    ) : BaseTimeEntity() {


    @ManyToOne
    @JoinColumn(name = "ISSUE_ID")
    var issue: Issue? = null

    fun update(issue: Hashtag): Hashtag? {
        this.name = issue.name
        return this
    }

    fun deleteIssue() {
        this.issue = null;
    }
}


