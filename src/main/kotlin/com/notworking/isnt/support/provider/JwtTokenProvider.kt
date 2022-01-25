package com.notworking.isnt.support.provider

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest


@Component
class JwtTokenProvider(
    @Value("\${spring.access-token.secret}") accessTokenSecret: String?,
    @Value("\${spring.refresh-token.secret}") refreshTokenSecret: String?
) {
    val AUTHORITIES_KEY = "auth"
    val BEARER_TYPE = "bearer"

    @Value("\${spring.access-token.expiration-time}")
    var ACCESS_TOKEN_EXPIRE_TIME = (1000 * 60 * 30).toLong()  // 30분

    @Value("\${spring.refresh-token.expiration-time}")
    var REFRESH_TOKEN_EXPIRE_TIME = (1000 * 60 * 60 * 24 * 7).toLong()  // 7일

    val log = LoggerFactory.getLogger(javaClass)
    val accessTokenkey: Key
    val refreshTokenkey: Key

    companion object {
        @JvmStatic
        val ACCESS_TOKEN_NAME: String = "Access-Token"

        @JvmStatic
        val REFRESH_TOKEN_NAME: String = "Refresh-Token"

    }

    init {
        accessTokenkey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret))
        refreshTokenkey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecret))
    }

    fun buildAccessToken(authentication: Authentication): String? {
        val authorities: String = authentication.getAuthorities().stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))
        val now: Long = Date().getTime()

        // Access Token 생성
        val accessTokenExpiresIn = Date(now + ACCESS_TOKEN_EXPIRE_TIME)
        return Jwts.builder()
            .setSubject(authentication.name) // payload "sub": "name"
            .claim(AUTHORITIES_KEY, authorities) // payload "auth": "ROLE_USER"
            .setExpiration(accessTokenExpiresIn) // payload "exp": 1516239022 (예시)
            .signWith(accessTokenkey, SignatureAlgorithm.HS512) // header "alg": "HS512"
            .compact()
    }

    fun buildRefreshToken(authentication: Authentication): String? {
        // 권한들 가져오기
        val authorities: String = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))
        val now: Long = Date().getTime()

        // Refresh Token 생성
        return Jwts.builder()
            .setExpiration(Date(now + REFRESH_TOKEN_EXPIRE_TIME))
            .signWith(refreshTokenkey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun resolveToken(req: HttpServletRequest): String? {
        return req.getHeader(ACCESS_TOKEN_NAME)
    }

    fun getAuthentication(accessToken: String): Authentication {
        // 토큰 복호화
        val claims = parseClaims(accessToken)
        if (claims[AUTHORITIES_KEY] == null) {
            throw RuntimeException("권한 정보가 없는 토큰입니다.")
        }

        // 클레임에서 권한 정보 가져오기
        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(claims[AUTHORITIES_KEY].toString().split(",").toTypedArray())
                .map { role: String? -> SimpleGrantedAuthority(role) }
                .collect(Collectors.toList())

        // UserDetails 객체를 만들어서 Authentication 리턴
        val principal: UserDetails = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateAccessToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(accessTokenkey).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
        }
        return false
    }

    fun validateRefreshToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(refreshTokenkey).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
        }
        return false
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(accessTokenkey).build().parseClaimsJws(accessToken).getBody()
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}