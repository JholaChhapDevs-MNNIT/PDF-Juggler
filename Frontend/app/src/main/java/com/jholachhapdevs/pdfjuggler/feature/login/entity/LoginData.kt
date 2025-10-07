package com.jholachhapdevs.pdfjuggler.feature.login.entity

import kotlinx.serialization.Serializable

//{"Status":"Success","Details":"aaac4d75-4303-11f0-a562-0200cd936042","OTP":"253473"}

@Serializable
data class OtpResponse (
    val Status: String,
    val Details: String,
    val OTP: String
)

//@Serializable
//data class LoginData(
//    val phone: Long,
//    val adminId: String
//)
//
//@Serializable
//data class LoginResponse(
//    val profileCompleted: Boolean,
//    val user: UserData? = null,
//    val accessToken: String? = null,
//    val msg: String? = null,
//)
//
//@Serializable
//data class StatusRequest(
//    val adminId: String,
//)
//
//@Serializable
//data class StatusResponse(
//    val isActive: Boolean,
//    val msg: String? = null
//)