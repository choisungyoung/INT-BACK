package com.notworking.isnt.model

import com.notworking.isnt.support.type.DocType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "INT_SOLUTION")
data class Solution(
    @Id
    @GeneratedValue
    var id: Long?,
    @Lob
    var content: String,
    var docType: DocType,

    ) : BaseTimeEntity() {

    var recommendationCount: Long = 0

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    @ManyToOne
    @JoinColumn(name = "ISSUE_ID")
    lateinit var issue: Issue

    fun update(issue: Solution): Solution? {
        this.content = issue.content
        this.docType = issue.docType
        return this
    }

    fun updateIssue(issue: Issue) {
        this.issue = issue

        // 무한루프에 빠지지 않도록 체크
        if (!issue.solutions.contains(this)) {
            issue.solutions.add(this)
        }
    }

    fun varyRecommendationCount(isIncrease: Boolean) {
        if (isIncrease)
            recommendationCount++
        else
            recommendationCount--
        this.modifiedDate = LocalDateTime.now()
    }
}


