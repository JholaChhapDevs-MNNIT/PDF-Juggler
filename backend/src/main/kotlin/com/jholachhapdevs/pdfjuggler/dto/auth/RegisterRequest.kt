package com.jholachhapdevs.pdfjuggler.dto.auth


import jakarta.validation.constraints.*

data class RegisterRequest(

    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Username can only contain letters, numbers, dots, underscores, and hyphens"
    )
    val username: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
        message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
    )
    val password: String,

//    @field:NotBlank(message = "Password confirmation is required")
//    val passwordConfirmation: String
) {
//    @AssertTrue(message = "Password confirmation must match password")
//    fun isPasswordConfirmationValid(): Boolean = password == passwordConfirmation
}