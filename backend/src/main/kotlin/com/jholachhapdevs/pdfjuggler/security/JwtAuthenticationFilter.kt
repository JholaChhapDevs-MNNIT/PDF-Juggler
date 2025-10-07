package com.jholachhapdevs.pdfjuggler.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        try {
            val token = extractTokenFromRequest(request)

            if (!token.isNullOrEmpty() && SecurityContextHolder.getContext().authentication == null) {
                try {
                    val username = jwtTokenProvider.extractUsername(token)

                    if (!username.isNullOrEmpty() && jwtTokenProvider.validateToken(token)) {
                        val userDetails = userDetailsService.loadUserByUsername(username)

                        if (userDetails != null && userDetails.isEnabled) {
                            val authentication = UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.authorities
                            )
                            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                            SecurityContextHolder.getContext().authentication = authentication
                        }
                    }
                } catch (ex: AuthenticationException) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: ${ex.message}")
                    return
                } catch (ex: RuntimeException) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: ${ex.message}")
                    return
                }
            }

            filterChain.doFilter(request, response)

        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication error: ${ex.message}")
        }
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    private fun sendErrorResponse(response: HttpServletResponse, statusCode: Int, message: String) {
        response.status = statusCode
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("{\"error\": \"${message.replace("\"", "\\\"")}\"}")
    }
}