package com.jholachhapdevs.pdfjuggler.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AuthRequest(

    @field:NotBlank(message = "Login identifier cannot be empty")
    @field:Size(min = 3, max = 50, message = "Login identifier must be between 3 and 50 characters")
    val loginIdentifier: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    val password: String
)