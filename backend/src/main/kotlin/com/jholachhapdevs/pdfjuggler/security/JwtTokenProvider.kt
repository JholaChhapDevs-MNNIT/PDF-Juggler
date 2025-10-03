package com.jholachhapdevs.pdfjuggler.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val userDetailsService: UserDetailsService
) {

    @Value("\${jwt.secret}")
    private lateinit var secretKeyBase64: String

    @Value("\${jwt.expiration}")
    private var validityInMilliseconds: Long = 0

    private lateinit var secretKey: SecretKey

    /**
     * Initializes the JWT signing key from the Base64-encoded secret.
     * Called after dependency injection is complete.
     */
    @PostConstruct
    fun init() {
        val keyBytes = Decoders.BASE64.decode(secretKeyBase64)
        secretKey = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * Creates a JWT token for a given username and roles.
     */
    fun createToken(username: String, roles: Collection<out GrantedAuthority>): String {
        val claims = Jwts.claims()
            .subject(username)
            .add("roles", roles.map { it.authority })
            .build()

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Extracts authentication information from the JWT token.
     */
    fun getAuthentication(token: String): Authentication {
        val username = extractUsername(token)
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)

        val authorities: Collection<GrantedAuthority> = extractRoles(token)
            .map { SimpleGrantedAuthority(it) }

        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    /**
     * Extracts the username from the JWT token.
     */
    fun extractUsername(token: String): String =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject

    /**
     * Extracts roles from the JWT token.
     */
    private fun extractRoles(token: String): Collection<String> {
        val rolesObj = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload["roles"]

        return if (rolesObj is Collection<*>) {
            rolesObj.map { it.toString() }
        } else {
            emptyList()
        }
    }

    /**
     * Extracts the token from the Authorization header.
     */
    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    /**
     * Validates the JWT token.
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)

            !claims.payload.expiration.before(Date())
        } catch (e: JwtException) {
            throw object : AuthenticationException("Expired or invalid JWT token") {}
        } catch (e: IllegalArgumentException) {
            throw object : AuthenticationException("Expired or invalid JWT token") {}
        }
    }

    /**
     * Checks if a token is expired.
     */
    fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .expiration
            expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Extracts the expiration date from the JWT token.
     */
    fun extractExpiration(token: String): Date =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .expiration

    /**
     * Refreshes an existing token with a new expiration date.
     */
    fun refreshToken(token: String): String {
        val createdDate = Date()
        val expirationDate = Date(createdDate.time + validityInMilliseconds)

        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        return Jwts.builder()
            .claims(claims)
            .issuedAt(createdDate)
            .expiration(expirationDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }
}