package com.notworking.isnt.model

import com.notworking.isnt.support.type.DocType
import javax.persistence.*

@Entity
@Table(name = "INT_ISSUE")
data class Issue(
    @Id
    @GeneratedValue
    var id: Long?,
    var title: String,
    @Lob
    var content: String,
    var docType: DocType,

    ) : BaseTimeEntity() {

    var hits: Long = 0
    var recommendationCount: Long = 0

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    fun update(issue: Issue): Issue? {
        this.title = issue.title
        this.content = issue.content
        this.docType = issue.docType
        return this
    }

    fun varyHits(isIncrease: Boolean) {
        if (isIncrease)
            hits++
        else
            hits--
    }

    fun varyRecommendationCount(isIncrease: Boolean) {
        if (isIncrease)
            recommendationCount++
        else
            recommendationCount--
    }
}


