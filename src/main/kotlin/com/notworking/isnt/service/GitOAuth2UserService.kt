package com.notworking.isnt.service

import com.notworking.isnt.model.Developer
import com.notworking.isnt.repository.DeveloperRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

/**
 * OAuth2 응답 처리를 위한 서비스
 */
@Service
class GitOAuth2UserService(
    val developerRepository: DeveloperRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    /**
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)
        val attributes = oAuth2User.attributes
        var developer: Developer? = developerRepository.findByUserId(attributes["id"].toString())

        if (developer == null) {
            developer = Developer(
                id = null,
                userId = attributes["id"].toString(),
                name = attributes["login"] as String,
                pwd = "",
                email = (attributes["email"] ?: "") as String,
                introduction = (attributes["bio"] ?: "") as String,
                gitUrl = (attributes["url"] ?: "") as String,
                webSiteUrl = (attributes["blog"] ?: "") as String,
                groupName = (attributes["company"] ?: "") as String,
                pictureUrl = (attributes["avatar_url"] ?: "") as String,
                0,
                0,
            )
        }

        // primary key를 의미
        val userNameAttributeName = userRequest.clientRegistration
            .providerDetails
            .userInfoEndpoint
            .userNameAttributeName
        // val member: Member = memberService.saveMember(m)
        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority(developer!!.role.key)),
            attributes, userNameAttributeName
        )
    }
}