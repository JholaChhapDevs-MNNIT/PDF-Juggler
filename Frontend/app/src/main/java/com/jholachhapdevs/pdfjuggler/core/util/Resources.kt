package com.jholachhapdevs.pdfjuggler.core.util

object Resources {
    // Default model used by AI features
    const val DEFAULT_AI_MODEL = "gemini-2.5-flash"

    // Provide your Gemini API key here or inject it via another mechanism at runtime.
    // Leaving this blank will cause network calls to fail with 401 until a valid key is set.
    const val GEMINI_API_KEY: String = ""
}
