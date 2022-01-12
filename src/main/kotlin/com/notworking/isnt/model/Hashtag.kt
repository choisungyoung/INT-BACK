package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_HASHTAG")
data class Hashtag(
    @Id
    @GeneratedValue
    var id: Long?,
    
    @Column(unique = true)
    var name: String,

    ) : BaseTimeEntity() {

    @OneToMany(mappedBy = "hashtag", cascade = [CascadeType.ALL])
    var issueHashtags: MutableList<IssueHashtag> = mutableListOf<IssueHashtag>()

    fun update(issue: Hashtag): Hashtag? {
        this.name = issue.name
        return this
    }

    fun addIssueHashtag(issueHashtag: IssueHashtag) {
        this.issueHashtags.add(issueHashtag)

        // 무한루프에 빠지지 않도록 체크
        if (issueHashtag.hashtag != this) {
            issueHashtag.hashtag = this
        }
    }
}


