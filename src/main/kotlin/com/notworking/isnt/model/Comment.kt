package com.notworking.isnt.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "INT_COMMENT")
data class Comment(
    @Id
    @GeneratedValue
    var id: Long?,
    var content: String,

    ) : BaseTimeEntity() {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEVELOPER_ID")
    lateinit var developer: Developer

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "SOLUTION_ID")
    var solution: Solution? = null

    fun update(issue: Comment): Comment? {
        this.content = issue.content
        this.modifiedDate = LocalDateTime.now()
        return this
    }

    fun deleteSolution() {
        this.solution = null;
    }
}


