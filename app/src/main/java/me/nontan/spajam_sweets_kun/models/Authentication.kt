package me.nontan.spajam_sweets_kun.models

data class AuthInfo(val user_id: Int, val token: String)
data class Authentication(val authentication: AuthInfo)
