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

    @OneToMany(mappedBy = "issue", cascade = [CascadeType.ALL])
    var solutions: MutableList<Solution> = ArrayList()
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "issue", cascade = [CascadeType.ALL])
    var hashtags: MutableList<Hashtag> = ArrayList()

    fun update(issue: Issue): Issue? {
        this.title = issue.title
        this.content = issue.content
        this.docType = issue.docType
        return this
    }

    fun increaseHit() {
        this.hits++
    }

    fun varyRecommendationCount(isIncrease: Boolean) {
        if (isIncrease)
            recommendationCount++
        else
            recommendationCount--
    }

    fun deleteHashtags() {
        hashtags.clear()
    }
}


