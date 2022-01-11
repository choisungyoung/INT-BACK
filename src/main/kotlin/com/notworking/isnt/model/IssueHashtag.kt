package com.notworking.isnt.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "INT_ISSUE_HASHTAG")
data class IssueHashtag(
    @Id
    @GeneratedValue
    var id: Long?,

    ) : BaseTimeEntity() {
    /*
    @ManyToOne
    @JoinColumn(name = "ISSUE_ID")
    lateinit var issue: Issue
*/
    /*
    @ManyToOne
    @JoinColumn(name = "HASHTAG_ID")
    lateinit var hashtag: Hashtag*/
}


