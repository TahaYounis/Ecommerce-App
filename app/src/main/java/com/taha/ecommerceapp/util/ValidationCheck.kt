package com.taha.ecommerceapp.util

import android.os.Build.VERSION_CODES.P
import android.util.Patterns

fun validationEmail(email: String): RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("Email can't be empty")
    if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches()) // this will return true if the email format correct
        // we used (!) so this check for wrong format
        return RegisterValidation.Failed("Wrong email format")
    return RegisterValidation.Success
}
fun validationPassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("Password can't be empty")
    if(password.length < 6)
        return RegisterValidation.Failed("Password should contains 6 char")
    return RegisterValidation.Success
}