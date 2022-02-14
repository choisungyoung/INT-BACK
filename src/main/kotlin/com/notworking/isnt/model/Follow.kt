package com.notworking.isnt.model

import javax.persistence.*

@Entity
@Table(name = "INT_FOLLOW")
data class Follow(
    @Id
    @GeneratedValue
    var id: Long?,
) : BaseTimeEntity() {

    @ManyToOne
    @JoinColumn(name = "FROM_DEVELOPER_ID")
    var fromDeveloper: Developer? = null

    @ManyToOne
    @JoinColumn(name = "TO_DEVELOPER_ID")
    var toDeveloper: Developer? = null
}


