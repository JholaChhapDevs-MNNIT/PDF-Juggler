package com.jholachhapdevs.pdfjuggler.dto.auth

import java.time.LocalDateTime

data class AuthResponse(
    val token: String,
    val userId: String,
    val username: String,
    val tokenExpiresAt: LocalDateTime
)