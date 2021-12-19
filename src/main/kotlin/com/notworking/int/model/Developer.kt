package com.notworking.int.model

import com.notworking.int.support.type.Role
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "INT_DEVELOPER")
data class Developer(
    @Id
    @GeneratedValue
    var id: Long?,
    var name: String?,
    var email: String,
    var pictureUrl: String?,
    var role: Role?
) : BaseTimeEntity() {

    fun update(developer: Developer): Developer? {
        this.name = developer.name
        this.email = developer.email
        this.pictureUrl = pictureUrl
        this.role = developer.role
        return this
    }
}


