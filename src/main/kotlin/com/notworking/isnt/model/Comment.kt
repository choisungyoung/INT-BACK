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

    @ManyToOne
    @JoinColumn(name = "SOLUTION_ID")
    lateinit var solution: Solution

    fun update(issue: Comment): Comment? {
        this.content = issue.content
        this.modifiedDate = LocalDateTime.now()
        return this
    }

    fun updateSolution(solution: Solution) {
        this.solution = solution

        // 무한루프에 빠지지 않도록 체크
        if (!solution.comments.contains(this)) {
            //solution.comments.add(this)
        }
    }
}


