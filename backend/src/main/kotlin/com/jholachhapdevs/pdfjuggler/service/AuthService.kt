package com.jholachhapdevs.pdfjuggler.service

import com.jholachhapdevs.pdfjuggler.dto.auth.AuthRequest
import com.jholachhapdevs.pdfjuggler.dto.auth.AuthResponse
import com.jholachhapdevs.pdfjuggler.dto.auth.RegisterRequest
import com.jholachhapdevs.pdfjuggler.entity.User
import com.jholachhapdevs.pdfjuggler.repository.UserRepository
import com.jholachhapdevs.pdfjuggler.security.JwtTokenProvider
import jakarta.validation.Valid
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {

    /**
     * Registers a new user with username, password, and phone number
     */
    @Transactional
    fun register(@Valid request: RegisterRequest): AuthResponse {
        validateRegistrationData(request)

        val user = User(
            uname = request.username,
            pwd = passwordEncoder.encode(request.password),
            phone = request.phone
        )

        val savedUser = userRepository.save(user)

        val token = jwtTokenProvider.createToken(savedUser.username, savedUser.authorities)
        val expirationDate = jwtTokenProvider.extractExpiration(token)
        val tokenExpiresAt = expirationDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return AuthResponse(
            token = token,
            userId = savedUser.id.toString(),
            username = savedUser.username,
            tokenExpiresAt = tokenExpiresAt
        )
    }

    /**
     * Ensures both username and phone are unique
     */
    private fun validateRegistrationData(request: RegisterRequest) {
        if (userRepository.existsByUname(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        if (userRepository.existsByPhone(request.phone)) {
            throw IllegalArgumentException("Phone number already exists")
        }
    }

    /**
     * Login using either username or phone number + password
     */
    @Transactional(readOnly = true)
    fun login(@Valid request: AuthRequest): AuthResponse {
        try {
            val user = findUserByLoginIdentifier(request.loginIdentifier)

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(user.username, request.password)
            )
            SecurityContextHolder.getContext().authentication = authentication

            val token = jwtTokenProvider.createToken(user.username, user.authorities)
            val expirationDate = jwtTokenProvider.extractExpiration(token)
            val tokenExpiresAt = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            return AuthResponse(
                token = token,
                userId = user.id.toString(),
                username = user.username,
                tokenExpiresAt = tokenExpiresAt
            )
        } catch (ex: BadCredentialsException) {
            throw BadCredentialsException("Invalid username, phone, or password")
        }
    }

    /**
     * Finds a user by username or phone number
     */
    private fun findUserByLoginIdentifier(identifier: String): User =
        userRepository.findByUname(identifier)
            ?: userRepository.findByPhone(identifier)
            ?: throw IllegalArgumentException("User not found with provided username or phone number")

    fun validateToken(token: String): Boolean {
        return try {
            jwtTokenProvider.validateToken(token)
        } catch (ex: RuntimeException) {
            false
        }
    }

    @Transactional(readOnly = true)
    fun existsByUsername(username: String): Boolean = userRepository.existsByUname(username)

    @Transactional(readOnly = true)
    fun existsByPhone(phone: String): Boolean = userRepository.existsByPhone(phone)
}
