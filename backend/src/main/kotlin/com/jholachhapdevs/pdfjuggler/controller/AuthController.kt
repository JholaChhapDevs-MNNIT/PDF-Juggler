package com.jholachhapdevs.pdfjuggler.controller


import com.jholachhapdevs.pdfjuggler.dto.auth.AuthRequest
import com.jholachhapdevs.pdfjuggler.dto.auth.AuthResponse
import com.jholachhapdevs.pdfjuggler.dto.auth.RegisterRequest
import com.jholachhapdevs.pdfjuggler.entity.User
import com.jholachhapdevs.pdfjuggler.service.AuthService
import com.jholachhapdevs.pdfjuggler.service.CurrentUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val currentUserService: CurrentUserService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(registerRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(authRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/validate-token")
    fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, Boolean>> {
        val isValid = authService.validateToken(token)
        val response = mapOf("valid" to isValid)
        return if (isValid) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
        }
    }
    data class UserResponse(
        val id: String,
        val username: String
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(): ResponseEntity<UserResponse?> {
        val user = currentUserService.getCurrentUserDetails()
        val response = UserResponse(
            id = user.id.toHexString(),
            username = user.username
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/check-username")
    fun checkUsernameAvailability(@RequestParam username: String): ResponseEntity<Map<String, Boolean>> {
        val isAvailable = !authService.existsByUsername(username)
        val response = mapOf("available" to isAvailable)
        return ResponseEntity.ok(response)
    }
}
