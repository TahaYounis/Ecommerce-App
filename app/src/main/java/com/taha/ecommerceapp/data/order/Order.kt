package com.taha.ecommerceapp.data.order

import android.os.Parcelable
import android.provider.ContactsContract.Data
import com.taha.ecommerceapp.data.Address
import com.taha.ecommerceapp.data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String= "",
    val totalPrice: Float= 0f,
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val data: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0,100_000_000_000) + totalPrice.toLong()
): Parcelable