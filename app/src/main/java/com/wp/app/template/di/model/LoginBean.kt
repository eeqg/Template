package com.wp.app.template.di.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoginBean(
    val userId: String,
    val displayName: String
)
