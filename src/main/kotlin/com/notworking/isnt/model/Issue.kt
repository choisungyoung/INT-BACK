package com.notworking.isnt.model

import com.notworking.isnt.support.type.DocType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "INT_ISSUE")
data class Issue(
    @Id
    @GeneratedValue
    var id: Long?,
    var title: String,
    var content: String,
    var docType: DocType,

    ) : BaseTimeEntity() {

    var hits: Long = 0
    var recommendationCount: Long = 0

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    fun update(issue: Issue): Issue? {
        this.title = issue.title
        this.content = issue.content
        this.docType = issue.docType
        this.modifiedDate = LocalDateTime.now()
        return this
    }

    fun varyHits(isIncrease: Boolean) {
        if (isIncrease)
            hits++
        else
            hits--
        this.modifiedDate = LocalDateTime.now()
    }

    fun varyRecommendationCount(isIncrease: Boolean) {
        if (isIncrease)
            recommendationCount++
        else
            recommendationCount--
        this.modifiedDate = LocalDateTime.now()
    }
}


