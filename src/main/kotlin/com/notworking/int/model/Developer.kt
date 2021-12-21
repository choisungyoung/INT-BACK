package com.notworking.int.model

import com.notworking.int.support.type.Role
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
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
    var pwd: String
) : UserDetails, BaseTimeEntity() {

    var role: Role = Role.USER

    fun update(developer: Developer): Developer? {
        this.email = developer.email
        this.pwd =developer.pwd
        this.name = developer.name
        this.pictureUrl = developer.pictureUrl
        this.role = developer.role
        return this
    }


    /** Spring Security */
    override fun getPassword(): String = pwd
    override fun getUsername(): String = email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        when(role) {
            Role.USER -> authorities.add(SimpleGrantedAuthority("ROLE_USER"))
            Role.ADMIN -> authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }
        return authorities
    }
}


