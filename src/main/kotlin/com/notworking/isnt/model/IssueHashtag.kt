package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_ISSUE_HASHTAG")
data class IssueHashtag(
    @Id
    @GeneratedValue
    var id: Long?,

    ) : BaseTimeEntity() {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ISSUE_ID")
    lateinit var issue: Issue

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HASHTAG_ID")
    lateinit var hashtag: Hashtag
}

