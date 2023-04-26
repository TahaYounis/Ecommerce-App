package com.taha.ecommerceapp.util

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}
// we will send an object of of this class to register fragment and check if state email is success or failed
data class RegisterFieldState(
    val email: RegisterValidation,
    val password: RegisterValidation,
)
