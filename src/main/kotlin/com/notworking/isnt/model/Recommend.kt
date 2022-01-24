package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_RECOMMEND")
data class Recommend(
    @Id
    @GeneratedValue
    var id: Long?,
    var recommendYn: Boolean = true
) : BaseTimeEntity() {

    @ManyToOne
    @JoinColumn(name = "SOLUTION_ID")
    var solution: Solution? = null

    @ManyToOne
    @JoinColumn(name = "DEVELOPER_ID")
    var developer: Developer? = null
}


