package com.notworking.isnt.model

import com.notworking.isnt.support.type.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "INT_DEVELOPER")
data class Developer(
    @Id
    @GeneratedValue
    var id: Long?,
    @Column(unique = true)
    var userId: String,
    @Column(unique = true)
    var name: String,
    var pwd: String,
    var email: String,
    var introduction: String?,

    var gitUrl: String?,
    var webSiteUrl: String?,
    var groupName: String?,

    var pictureUrl: String?,
    var point: Int?,
    var popularity: Int?,
) : UserDetails, BaseTimeEntity() {

    var role: Role = Role.USER

    fun update(developer: Developer): Developer? {
        this.email = developer.email
        this.name = developer.name
        this.introduction = developer.introduction
        this.pictureUrl = developer.pictureUrl
        this.groupName = developer.groupName
        this.point = developer.point
        this.popularity = developer.popularity

        this.role = developer.role
        this.modifiedDate = LocalDateTime.now()
        return this
    }


    /** Spring Security */
    override fun getPassword(): String = pwd
    override fun getUsername(): String = userId
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        when (role) {
            Role.USER -> authorities.add(SimpleGrantedAuthority("ROLE_USER"))
            Role.ADMIN -> authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }
        return authorities
    }
}


