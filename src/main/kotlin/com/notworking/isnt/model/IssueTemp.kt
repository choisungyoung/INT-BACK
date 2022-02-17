package com.notworking.isnt.model

import com.notworking.isnt.support.type.DocType
import javax.persistence.*


@Entity
@Table(name = "INT_ISSUE_TEMP")
data class IssueTemp(
    @Id
    @GeneratedValue
    var id: Long?,
    var title: String,
    @Lob
    var content: String,
    var docType: DocType,
) : BaseTimeEntity() {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    fun update(issue: IssueTemp): IssueTemp? {
        this.title = issue.title
        this.content = issue.content
        this.docType = issue.docType
        return this
    }
}


