package com.taha.ecommerceapp.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath:String = "",
){
    // whenever we deal with firebase we need empty constructor
    constructor(): this("","","","")

}
