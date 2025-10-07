package com.jholachhapdevs.pdfjuggler.service


import com.jholachhapdevs.pdfjuggler.entity.User
import com.jholachhapdevs.pdfjuggler.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CurrentUserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getCurrentUsername(): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authenticated user found")

        if (!authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            throw IllegalStateException("No authenticated user found")
        }

        return authentication.name
    }

    @Transactional(readOnly = true)
    fun getCurrentUserDetails(): User {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authenticated user found")

        if (!authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            throw IllegalStateException("No authenticated user found")
        }

        val user = authentication.principal as User
        return userRepository.findByUname(user.username)
            ?: throw IllegalStateException("Authenticated user not found in database")
    }
}
