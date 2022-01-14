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
    var adoptYn: Boolean = false

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    @ManyToOne
    @JoinColumn(name = "ISSUE_ID")
    var issue: Issue? = null

    @OneToMany(mappedBy = "solution", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf<Comment>()

    fun update(issue: Solution): Solution? {
        this.content = issue.content
        this.docType = issue.docType
        return this
    }

    fun varyRecommendationCount(isIncrease: Boolean) {
        if (isIncrease)
            recommendationCount++
        else
            recommendationCount--
        this.modifiedDate = LocalDateTime.now()
    }

    fun deleteIssue() {
        this.issue = null
    }

    fun deleteComments() {
        this.comments.forEach {
            it.deleteSolution();
        }
        comments.clear()
    }

}


