package com.jholachhapdevs.pdfjuggler.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.jholachhapdevs.pdfjuggler.feature.login.entity.OtpResponse
import com.kanhaji.basics.networking.httpClient
import com.kanhaji.basics.util.Env
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay // Add this import

class LoginScreenModel : ScreenModel {

    var isLoading by mutableStateOf(false)

//    suspend fun login(
//        phoneNumber: String,
//        onSuccess: (LoginResponse) -> Unit = {},
//        onError: (String) -> Unit = {}
//    ) {
//        try {
//            println("LoginApi: Trying to login with phone number: $phoneNumber")
//            val response = httpClient.post(ResourceProvider.current.apiUrl + "/api/user/login") {
//                contentType(ContentType.Application.Json)
//                setBody(LoginData(phoneNumber.toLong(), ResourceProvider.current.adminId))
//            }
//            println("LoginApi: login response: $response")
//            val responseObj: LoginResponse = response.body()
//            if (response.status.value in 200..299) {
//                onSuccess(responseObj)
//            } else {
//                onError("Login failed with status: ${response.status.value}")
//            }
//        } catch (e: Exception) {
//            // Handle error, e.g., show a toast or log the error
//            e.printStackTrace()
//            onError(e.message ?: "An error occurred during login")
//        }
//    }

    // New function to initiate sending OTP
    suspend fun initiateOtpRequest(
        phoneNumber: String,
        onSuccess: (OtpResponse) -> Unit = {},
        onFailure: (String) -> Unit = {},
    ) {
        try {
            val url = "https://2factor.in/API/V1/${Env.otpApiKey}/SMS/$phoneNumber/AUTOGEN2"
            println("Sending OTP request to URL: $url")
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }
            println("response: $response")
            val responseObj: OtpResponse = response.body()
//            delay(1000L) // Simulate network delay
//            val responseObj = OtpResponse(
//                Status = "Success",
//                Details = "743a498f2c232ddb",
//                OTP = "000000" // Simulated OTP for testing
//            ) // Simulated response for testing
            println("OTP response: $responseObj")
            onSuccess(responseObj)
        } catch (e: Exception) {
            // Handle error, e.g., show a toast or log the error
            e.printStackTrace()
            onFailure(e.message ?: "An error occurred while sending OTP")
            return
        }
    }

//    suspend fun status(
//        phoneNumber: String,
//        onSuccess: () -> Unit,
//        onFailure: () -> Unit
//    ) {
//        // Simulate network delay for verifying the phone number
////        delay(1000L)
//        try {
//            val response = httpClient.post(ResourceProvider.current.apiUrl + "/api/user/status") {
//                contentType(ContentType.Application.Json)
//                setBody(StatusRequest(ResourceProvider.current.adminId))
//            }
//
//            println("Response: $response")
//
//            val responseObj: StatusResponse = response.body()
//            println("Status response: $responseObj")
//            if (responseObj.isActive) onSuccess()
//        } catch (e: Exception) {
//            // Handle error, e.g., show a toast or log the error
//            e.printStackTrace()
//            onFailure()
//            return
//        }
//
//
//        // In a real app, you would make an API call here to verify the phone number and OTP.
//        // For this example, we'll assume it's successful.
//    }
}